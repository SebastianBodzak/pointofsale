package pl.com.sebastianbodzak.infrastructure.repositories;

import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.ProductRepository;

import java.sql.*;

import static pl.com.sebastianbodzak.domain.Money.CurrencyValues;
import static pl.com.sebastianbodzak.domain.Money.parseCentsIntoMoney;

/**
 * Created by Dell on 2016-08-08.
 */
public class JDBCWareHouseProductRepository implements ProductRepository {

    private final String url;
    private final String login;
    private final String password;

    public JDBCWareHouseProductRepository(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    @Override
    public Product load(BarCode barCode) throws DataAccessException {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT Products.id, Products.name, Products.nettoPriceCents, Products.bruttoPriceCents, Products.priceCurrency, " +
                            "ProductsTypes.type\n" +
                            "FROM Products\n" +
                            "JOIN ProductsTypes ON Products.typeId = ProductsTypes.id\n" +
                            "WHERE Products.barCode = ?;");
            ps.setString(1, barCode.getCode());
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                return null;

            CurrencyValues currency = CurrencyValues.valueOf(rs.getString("priceCurrency"));
            Money netto = parseCentsIntoMoney(rs.getInt("nettoPriceCents"), currency);
            Money brutto = parseCentsIntoMoney(rs.getInt("bruttoPriceCents"), currency);
            ProductType type = ProductType.valueOf(rs.getString("ProductsTypes.type"));
            return new Product(rs.getInt("id"), barCode, rs.getString("name"), netto, brutto, type);
        } catch (Exception ex) {
            throw new DataAccessException("Product not found", barCode, JDBCWareHouseProductRepository.class);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, login, password);
    }

    @Override
    public void save(Product product) {
        try (Connection c = getConnection()) {
            try {
                c.setAutoCommit(false);
                String query = productExists(product) ?
                        "UPDATE Products SET name = ?, typeId = ?, nettoPriceCents = ?, bruttoPriceCents = ?, priceCurrency = ? WHERE barCode = ?"
                        : "INSERT INTO Products (barCode, name, typeId, nettoPriceCents, bruttoPriceCents, priceCurrency) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = c.prepareStatement(query);
                if (productExists(product))
                    ps = prepareProductToUpdate(c, ps, product);
                else
                    ps = prepareProductToInsert(c, ps, product);
                ps.executeUpdate();
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        } catch (Exception ex){
            throw new DataAccessException("Can not save product", JDBCWareHouseProductRepository.class );
        }
    }

    private PreparedStatement prepareProductToInsert(Connection c, PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getBarCode().getCode());
        ps.setString(2, product.getName());
        Integer typeId = getTypeId(c, product.getProductType().toString());
        if (typeId == null)
            typeId = insertProductType(c, product.getProductType().toString());
        ps.setInt(3, typeId);
        ps.setInt(4, product.getNettoPrice().parseMoneyIntoCents());
        ps.setInt(5, product.getBruttoPrice().parseMoneyIntoCents());
        ps.setString(6, product.getBruttoPrice().getCurrency().getCurrencyCode());
        return ps;
    }

    private PreparedStatement prepareProductToUpdate(Connection c, PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getName());
        Integer typeId = getTypeId(c, product.getProductType().toString());
        if (typeId == null)
            typeId = insertProductType(c, product.getProductType().toString());
        ps.setInt(2, typeId);
        ps.setInt(3, product.getNettoPrice().parseMoneyIntoCents());
        ps.setInt(4, product.getBruttoPrice().parseMoneyIntoCents());
        ps.setString(5, product.getBruttoPrice().getCurrency().getCurrencyCode());
        ps.setString(6, product.getBarCode().getCode());
        return ps;
    }

    private Integer insertProductType(Connection c, String productType) throws SQLException {
        PreparedStatement ps = c.prepareStatement("INSERT INTO ProductsTypes (type) VALUES (?)");
        ps.setString(1, productType);
        ps.executeUpdate();
        return getTypeId(c, productType);
    }

    private Integer getTypeId(Connection c, String productType) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT id FROM ProductsTypes WHERE type = ?");
        ps.setString(1, productType);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
            return rs.getInt("id");
        return null;
    }

    private boolean productExists(Product product) {
        return load(product.getBarCode()) != null;
    }
}