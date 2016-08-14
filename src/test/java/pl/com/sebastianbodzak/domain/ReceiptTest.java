package pl.com.sebastianbodzak.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;

import java.util.Date;

import static org.junit.Assert.*;
import static pl.com.sebastianbodzak.domain.ProductType.COSMETICS;

/**
 * Created by Dell on 2016-08-04.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceiptTest {

    private static final Money NETTO_PRICE = new Money(15);
    private static final Money BRUTTO_PRICE_GB_COSMETICS_TAX = new Money(15.75);
    private static final Money SUM_NETTO = new Money(30);
    private static final Money SUM_BRUTTO = BRUTTO_PRICE_GB_COSMETICS_TAX.multiply(2);
    private static final ProductType TYPE = COSMETICS;
    private static final String NAME = "test name";
    private static final Long EPS = 2L * 1000L;
    private static final TaxPolicy POLICY = new GBStrategy();

    @Mock
    private Product product;

    @Mock
    private Product secondProduct;

    @Mock
    private BarCode barCode;

    private Receipt receipt;
    private Product realProduct;
    private Product realProduct2;

    @Before
    public void setUp() {
        receipt = new Receipt();
        realProduct = new Product(new BarCode("test code"), NAME, NETTO_PRICE, TYPE, POLICY);
        realProduct2 = new Product(new BarCode("test code 2"), NAME, NETTO_PRICE, TYPE, POLICY);
    }

    @Test
    public void shouldCreateNewReceipt() {

        assertNotNull(receipt.getReceiptNumber());
        assertTrue(receipt.getProducts().size() == 0);
        assertFalse(receipt.isClosed());
    }

    @Test
    public void shouldAddProduct() {
        receipt.add(realProduct);

        assertEquals(realProduct, receipt.getProducts().get(0));
        assertTrue(receipt.getProducts().size() == 1);
    }

    @Test
    public void shouldAddTwoProduct() {
        receipt.add(realProduct);
        receipt.add(realProduct2);

        assertTrue(receipt.getProducts().size() == 2);
    }

    @Test
    public void shouldRemoveProduct() {
        receipt.add(realProduct);
        receipt.add(realProduct2);

        receipt.remove(realProduct);

        assertTrue(receipt.getProducts().size() == 1);
        assertEquals(realProduct2, receipt.getProducts().get(0));
    }

    @Test
    public void shouldSumProductsNettoPrices() {
        receipt.add(realProduct);
        receipt.add(realProduct2);

        Money sum = receipt.getTotalNettoSum();
        assertEquals(SUM_NETTO, sum);
    }

    @Test
    public void shouldSumProductsBruttoPrices() {
        receipt.add(realProduct);
        receipt.add(realProduct2);

        Money sum = receipt.getTotalBruttoSum();
        assertEquals(SUM_BRUTTO, sum);
    }

    @Test
    public void shouldConfirmReceipt() {
        receipt.add(realProduct);
        receipt.add(realProduct2);

        receipt.confirm();

        Money sum = receipt.getTotalBruttoSum();
        assertEquals(SUM_BRUTTO, sum);
        assertTrue(receipt.isClosed());
        assertTrue(Math.abs(new Date().getTime() - receipt.getConfirmationDate().getTime()) < EPS);
    }

}
