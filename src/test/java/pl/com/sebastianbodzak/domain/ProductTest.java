package pl.com.sebastianbodzak.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pl.com.sebastianbodzak.domain.ProductType.*;

/**
 * Created by Dell on 2016-08-04.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductTest {

    private static final Money MONEY_AMOUNT = new Money(5.50);
    private static final Money MONEY_BRUTTO_AMOUNT_ALHOCOL_GB_TAX = new Money(6.60);
    private static final ProductType PRODUCT_TYPE = CIGARETTES;
    private static final TaxPolicy GB_POLICY = new GBStrategy();
    private static final BarCode BAR_CODE = new BarCode("Test bar-code");

    @Mock
    private BarCode barCode;

    private String name = "test product name";

    @Mock
    private Money nettoPrice;

    @Mock
    private Money bruttoPrice;

    private Product product;

    @Before
    public void setUp() {
        product = new Product(barCode, name, MONEY_AMOUNT, PRODUCT_TYPE, GB_POLICY);
    }

    @Test
    public void shouldCreateNewProduct() {

        assertEquals(barCode, product.getBarCode());
        assertEquals(name, product.getName());
        assertEquals(MONEY_AMOUNT, product.getNettoPrice());
        assertEquals(PRODUCT_TYPE, product.getProductType());
    }

    @Test
    public void shouldHasCorrectBruttoPriceByTaxPolicy() {

        assertEquals(MONEY_BRUTTO_AMOUNT_ALHOCOL_GB_TAX, product.getBruttoPrice());
    }

    @Test
    public void shouldTwoProductsBeEqual() {
        Product product = new Product(BAR_CODE, name, nettoPrice, PRODUCT_TYPE, GB_POLICY);
        Product product2 = new Product(BAR_CODE, "another name", new Money(22), PRODUCT_TYPE, GB_POLICY);

        boolean result = product.equals(product2);

        assertTrue(result);
    }

    @Test
    public void shouldNotTwoProductsBeEqual() {
        Product product = new Product(barCode, name, nettoPrice, PRODUCT_TYPE, GB_POLICY);
        Product product2 = new Product(BAR_CODE, name, nettoPrice, PRODUCT_TYPE, GB_POLICY);

        boolean result = product.equals(product2);

        assertFalse(result);
    }
}
