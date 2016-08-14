package pl.com.sebastianbodzak.infrastructure.repositories;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.ProductRepository;
import pl.com.sebastianbodzak.domain.ReceiptRepository;

import java.sql.*;

/**
 * Created by Dell on 2016-08-09.
 */
public class JDBCCentralReceiptRepositoryTest {

    private ReceiptRepository receiptRepository;
    private ProductRepository productRepository;

    @Before
    public void setUp() throws Exception {
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:pointofsale", "SA", "");
        dropTables(c);
        createProductsTypesTable(c);
        createProductsTable(c);
        createReceiptsTable(c);
        createReceiptsProductsTable(c);
        insertProductsTypes(c);
        insertProducts(c);
        insertReceiptWithProducts(c);
        c.close();

        receiptRepository = new JDBCCentralReceiptRepository("jdbc:hsqldb:mem:pointofsale", "SA", "");
        productRepository = new JDBCWareHouseProductRepository("jdbc:hsqldb:mem:pointofsale", "SA", "");
    }

    @Test
    public void shouldLoadReceipt() throws Exception {
        Receipt receipt = receiptRepository.load(new ReceiptNumber("test number"));

        assertEquals("test number", receipt.getReceiptNumber().getNumber());
        assertFalse(receipt.isClosed());
        assertEquals(2, receipt.getProducts().size());
    }

    @Test
    public void shouldReturnNullWhenProductDoesntExists() throws Exception {
        Receipt receipt = receiptRepository.load(new ReceiptNumber("nr500"));

        assertNull(receipt);
    }

    @Test
    public void shouldSaveReceiptWithProducts() {
        Receipt receipt = new Receipt();
        Product product = productRepository.load(new BarCode("barCode1"));
        Product product2 = productRepository.load(new BarCode("barCode2"));
        receipt.add(product);
        receiptRepository.save(receipt);
        receipt.add(product2);

        receiptRepository.save(receipt);

        Receipt savedReceipt = receiptRepository.load(receipt.getReceiptNumber());
        assertEquals(2, savedReceipt.getProducts().size());
        assertEquals(new Money(50), savedReceipt.getTotalNettoSum());

    }

    @Test
    public void shouldUpdateReceiptWithProduct() {
        Receipt receipt = receiptRepository.load(new ReceiptNumber("test number"));
        Product product3 = productRepository.load(new BarCode("barCode3"));
        receipt.add(product3);

        receiptRepository.save(receipt);

        Receipt savedReceipt = receiptRepository.load(new ReceiptNumber("test number"));
        assertEquals(3, savedReceipt.getProducts().size());
        assertEquals(new Money(63).getValue(), savedReceipt.getTotalBruttoSum().getValue());
    }

    @Test
    public void shouldRemoveReceipt() throws SQLException {
        Receipt receipt = receiptRepository.load(new ReceiptNumber("test number"));

        receiptRepository.delete(receipt);

        assertNull(receiptRepository.load(new ReceiptNumber("test number")));
    }

    @Test
    public void shouldRemoveReceiptReferenceFromReceiptsProductsTable() throws SQLException {
        Receipt receipt = receiptRepository.load(new ReceiptNumber("test number"));

        receiptRepository.delete(receipt);

        boolean refenceStillExists = checkIfRecepitHasProducts(receipt.getId());
        assertNull(receiptRepository.load(new ReceiptNumber("test number")));
        assertFalse(refenceStillExists);

    }

    private void dropTables(Connection c) throws SQLException {
        c.createStatement().executeUpdate("DROP TABLE ReceiptsProducts IF EXISTS");
        c.createStatement().executeUpdate("DROP TABLE Receipts IF EXISTS");
        c.createStatement().executeUpdate("DROP TABLE Products IF EXISTS");
        c.createStatement().executeUpdate("DROP TABLE ProductsTypes IF EXISTS");
    }

    private void createProductsTable(Connection c) throws SQLException {
        c.createStatement().executeUpdate("CREATE TABLE Products (\n" +
                "id INTEGER IDENTITY PRIMARY KEY,\n" +
                "barCode VARCHAR(36) NOT NULL,\n" +
                "name VARCHAR(255) NOT NULL,\n" +
                "typeId INTEGER FOREIGN KEY REFERENCES ProductsTypes(id),\n" +
                "nettoPriceCents INTEGER DEFAULT 0 NOT NULL,\n" +
                "bruttoPriceCents INTEGER DEFAULT 0 NOT NULL,\n" +
                "priceCurrency CHAR(3) DEFAULT 'PLN' NOT NULL\n" +
                ");");
    }

    private void createProductsTypesTable(Connection c) throws SQLException {
        c.createStatement().executeUpdate("CREATE TABLE ProductsTypes (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  type VARCHAR(20) NOT NULL\n" +
                ");");
    }

    private void createReceiptsTable(Connection c) throws SQLException {
        c.createStatement().executeUpdate("CREATE TABLE Receipts (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  number VARCHAR(36) NOT NULL,\n" +
                "  closed BOOLEAN DEFAULT FALSE NOT NULL,\n" +
                "  totalNettoSumCents INTEGER DEFAULT 0 NOT NULL,\n" +
                "  totalBruttoSumCents INTEGER DEFAULT 0 NOT NULL,\n" +
                "  priceCurrency CHAR(3) DEFAULT 'EUR' NOT NULL,\n" +
                "  confirmDate TIMESTAMP DEFAULT NULL\n" +
                ");");
    }

    private void createReceiptsProductsTable(Connection c) throws SQLException {
        c.createStatement().executeUpdate("CREATE TABLE ReceiptsProducts (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  receiptId INTEGER FOREIGN KEY REFERENCES Receipts(id),\n" +
                "  productId INTEGER FOREIGN KEY REFERENCES Products(id)\n" +
                ");");
    }

    private void insertProductsTypes(Connection c) throws SQLException {
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('CLOTHES');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('VEGETABLE');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('CIGARETTES');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('COSMETICS');");
    }

    private void insertProducts(Connection c) throws Exception {
        c.createStatement().executeUpdate(
                "INSERT INTO Products (barCode, name, typeId, nettoPriceCents, bruttoPriceCents, priceCurrency)\n" +
                        "VALUES ('barCode1', 'testProduct', 0, 2000, 2100, 'EUR');");
        c.createStatement().executeUpdate(
                "INSERT INTO Products (barCode, name, typeId, nettoPriceCents, bruttoPriceCents, priceCurrency)\n" +
                        "VALUES ('barCode2', 'testProduct2', 1, 3000, 3000, 'EUR');");
        c.createStatement().executeUpdate(
                "INSERT INTO Products (barCode, name, typeId, nettoPriceCents, bruttoPriceCents, priceCurrency)\n" +
                        "VALUES ('barCode3', 'testProduct3', 2, 1000, 1200, 'EUR');");
    }

    private void insertReceiptWithProducts(Connection c) throws SQLException {
        c.createStatement().executeUpdate("INSERT INTO Receipts (number, closed, totalNettoSumCents, totalBruttoSumCents, priceCurrency)" +
                " VALUES ('test number', FALSE, 5000, 5100, 'EUR')");
        c.createStatement().executeUpdate("INSERT INTO ReceiptsProducts (receiptId, productId) VALUES (0, 0)");
        c.createStatement().executeUpdate("INSERT INTO ReceiptsProducts (receiptId, productId) VALUES (0, 1)");
    }

    private boolean checkIfRecepitHasProducts(Integer id) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:pointofsale", "SA", "");
        PreparedStatement ps = c.prepareStatement("SELECT * FROM ReceiptsProducts WHERE receiptId = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
