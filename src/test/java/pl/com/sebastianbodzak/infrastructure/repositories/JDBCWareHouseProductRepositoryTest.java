package pl.com.sebastianbodzak.infrastructure.repositories;

import org.junit.Before;
import org.junit.Test;
import pl.com.sebastianbodzak.domain.BarCode;
import pl.com.sebastianbodzak.domain.Money;
import pl.com.sebastianbodzak.domain.Product;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;
import pl.com.sebastianbodzak.domain.ProductRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static pl.com.sebastianbodzak.domain.ProductType.BREAD;
import static pl.com.sebastianbodzak.domain.ProductType.JEWELRY;

/**
 * Created by Dell on 2016-08-09.
 */
public class JDBCWareHouseProductRepositoryTest {

    private ProductRepository productRepository;

    @Before
    public void setUp() throws Exception {
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:pointofsale", "SA", "");
        dropTables(c);
        createProductsTypesTable(c);
        createProductsTable(c);
        insertTestProductsType(c);
        insertTestProduct(c);
        c.close();

        productRepository = new JDBCWareHouseProductRepository("jdbc:hsqldb:mem:pointofsale", "SA", "");
    }

    @Test
    public void shouldLoadProduct() throws Exception {
        Product product = productRepository.load(new BarCode("barCode1"));

        assertEquals("barCode1", product.getBarCode().getCode());
        assertEquals("testProduct", product.getName());
    }

    @Test
    public void shouldReturnNullWhenProductDoesntExists() throws Exception {
        Product product = productRepository.load(new BarCode("nr500"));

        assertNull(product);
    }

    @Test
    public void shouldSaveProductWithExistingType() {
        Product product = new Product(new BarCode("shouldInsertProduct"), "insert product", new Money(20), BREAD, new GBStrategy());

        productRepository.save(product);

        Product saved = productRepository.load(new BarCode("shouldInsertProduct"));
        assertEquals("insert product", saved.getName());
        assertEquals(new Money(20), saved.getNettoPrice());

    }

    @Test
    public void shouldSaveProductWithoutExistingType() {
        Product product = new Product(new BarCode("product"), "insert product", new Money(20), JEWELRY, new GBStrategy());

        productRepository.save(product);

        Product saved = productRepository.load(new BarCode("product"));
        assertEquals("insert product", saved.getName());
    }

    @Test
    public void shouldUpdateProductWithType() {
        Product product = new Product(new BarCode("test"), "insert product", new Money(20), BREAD, new GBStrategy());
        Product updatedProduct = new Product(new BarCode("test"), "insert product", new Money(20), JEWELRY, new GBStrategy());
        productRepository.save(product);

        productRepository.save(updatedProduct);

        Product saved = productRepository.load(new BarCode("test"));
        assertEquals("insert product", saved.getName());
        assertEquals(JEWELRY, saved.getProductType());

    }

    private void dropTables(Connection c) throws SQLException {
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

    private void insertTestProductsType(Connection c) throws SQLException {
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('BREAD');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('VEGETABLE');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('FRUIT');");
        c.createStatement().executeUpdate("INSERT INTO ProductsTypes (type) VALUES ('COSMETICS');");
    }

    private void insertTestProduct(Connection c) throws Exception {
        c.createStatement().executeUpdate(
                "INSERT INTO Products (barCode, name, typeId, nettoPriceCents, bruttoPriceCents, priceCurrency)\n" +
                "VALUES ('barCode1', 'testProduct', 1, 2000, 2460, 'USD');");
    }
}
