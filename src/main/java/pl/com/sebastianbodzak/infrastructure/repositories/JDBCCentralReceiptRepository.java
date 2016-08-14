package pl.com.sebastianbodzak.infrastructure.repositories;

import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.ReceiptRepository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static pl.com.sebastianbodzak.domain.Money.parseCentsIntoMoney;

/**
 * Created by Dell on 2016-08-08.
 */
public class JDBCCentralReceiptRepository implements ReceiptRepository {

    private final String url;
    private final String login;
    private final String password;

    public JDBCCentralReceiptRepository(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    @Override
    public Receipt load(ReceiptNumber receiptNumber) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id, closed, totalNettoSumCents, totalBruttoSumCents, priceCurrency, confirmDate \n" +
                            "FROM Receipts \n" +
                            "WHERE number = ?;");
            ps.setString(1, receiptNumber.getNumber());
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                return null;

            Money.CurrencyValues currency = Money.CurrencyValues.valueOf(rs.getString("priceCurrency"));
            Money netto = parseCentsIntoMoney(rs.getInt("totalNettoSumCents"), currency);
            Money brutto = parseCentsIntoMoney(rs.getInt("totalBruttoSumCents"), currency);
            return new Receipt(rs.getInt("id"), receiptNumber, rs.getBoolean("closed"),
                    netto, brutto, loadProductList(c, rs.getInt("id")), rs.getDate("confirmDate"));
        } catch (Exception ex) {
            throw new DataAccessException("Can not load receipt", JDBCCentralReceiptRepository.class);
        }
    }

    private List<Product> loadProductList(Connection c, int id) throws SQLException {
        List<Product> result = new LinkedList<>();
        PreparedStatement ps = c.prepareStatement("SELECT Products.id, Products.barCode, Products.name, Products.nettoPriceCents, " +
                "Products.bruttoPriceCents, Products.priceCurrency, ProductsTypes.type\n" +
                "FROM Products\n" +
                "  JOIN ProductsTypes ON Products.typeId = ProductsTypes.id\n" +
                "  JOIN ReceiptsProducts ON Products.id = ReceiptsProducts.productId\n" +
                "  JOIN Receipts ON ReceiptsProducts.receiptId = Receipts.id\n" +
                "WHERE Receipts.id = ?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
           Money.CurrencyValues currency = Money.CurrencyValues.valueOf(rs.getString("priceCurrency"));
            Money netto = parseCentsIntoMoney(rs.getInt("nettoPriceCents"), currency);
            Money brutto = parseCentsIntoMoney(rs.getInt("bruttoPriceCents"), currency);
            ProductType type = ProductType.valueOf(rs.getString("ProductsTypes.type"));
            result.add(new Product(rs.getInt("id"), new BarCode(rs.getString("Products.barCode")), rs.getString("name"), netto, brutto, type));
        }
        return result;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, login, password);
    }

    @Override
    public void save(Receipt receipt) {
        try (Connection c = getConnection()) {
            try {
                c.setAutoCommit(false);
                String query = receiptExists(receipt) ?
                        "UPDATE Receipts SET closed = ?, totalNettoSumCents = ?, totalBruttoSumCents = ?, priceCurrency = ?, confirmDate = ? " +
                                "WHERE number = ?"
                        : "INSERT INTO Receipts (number, closed, totalNettoSumCents, totalBruttoSumCents, priceCurrency, confirmDate) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = c.prepareStatement(query);
                if (receiptExists(receipt))
                    ps = prepareReceiptToUpdate(ps, receipt);
                else
                    ps = prepareReceiptToInsert(ps, receipt);
                ps.executeUpdate();
                if (receipt.getProducts().size() != 0)
                    linkProduct(c, receipt);
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception ex){
            throw new DataAccessException("Can not save product", JDBCWareHouseProductRepository.class );
        }
    }

    private PreparedStatement prepareReceiptToUpdate(PreparedStatement ps, Receipt receipt) throws SQLException {
        ps.setBoolean(1, receipt.isClosed());
        ps.setInt(2, receipt.getTotalNettoSum().parseMoneyIntoCents());
        ps.setInt(3, receipt.getTotalBruttoSum().parseMoneyIntoCents());
        ps.setString(4, receipt.getTotalBruttoSum().getCurrency().getCurrencyCode());
        if (receipt.getConfirmationDate() == null)
            ps.setNull(5, Types.DATE);
        else
            ps.setDate(5, new Date(receipt.getConfirmationDate().getTime()));
        ps.setString(6, receipt.getReceiptNumber().getNumber());
        return ps;
    }

    private PreparedStatement prepareReceiptToInsert(PreparedStatement ps, Receipt receipt) throws SQLException {
        ps.setString(1, receipt.getReceiptNumber().getNumber());
        ps.setBoolean(2, receipt.isClosed());
        ps.setInt(3, receipt.getTotalNettoSum().parseMoneyIntoCents());
        ps.setInt(4, receipt.getTotalBruttoSum().parseMoneyIntoCents());
        ps.setString(5, receipt.getTotalBruttoSum().getCurrency().getCurrencyCode());
        if (receipt.getConfirmationDate() == null)
            ps.setNull(6, Types.DATE);
        else
            ps.setDate(6, new Date(receipt.getConfirmationDate().getTime()));
        return ps;
    }

    private void linkProduct(Connection c, Receipt receipt) throws SQLException {
        Integer receiptId = getReceiptId(c, receipt.getReceiptNumber().getNumber());
        insertProduct(c, receipt.getLastProduct().getId(), receiptId);
    }

    private void insertProduct(Connection c, Integer productId, Integer receiptId) throws SQLException {
        String query = "INSERT INTO ReceiptsProducts (receiptId, productId) VALUES (?, ?)";

        PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, receiptId);
            ps.setInt(2, productId);
        ps.executeUpdate();
    }

    private Integer getReceiptId(Connection c, String number) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT id FROM Receipts WHERE number = ?");
        ps.setString(1, number);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private boolean receiptExists(Receipt receipt) {
        return load(receipt.getReceiptNumber()) != null;
    }

    @Override
    public void delete(Receipt receipt) throws IllegalArgumentException, SQLException {
        if (!receiptExists(receipt))
            throw new IllegalArgumentException("Invalid receipt");

        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM ReceiptsProducts\n" +
                    "WHERE receiptId = ?");
            ps.setInt(1, receipt.getId());
            ps.executeUpdate();

            ps = c.prepareStatement("DELETE FROM Receipts\n" +
                    "WHERE id = ?");
            ps.setInt(1, receipt.getId());
            ps.executeUpdate();

        } catch (Exception ex) {
            throw new DataAccessException("Can not delete receipt", JDBCCentralReceiptRepository.class);
        }
    }
}
