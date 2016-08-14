package pl.com.sebastianbodzak.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;
import pl.com.sebastianbodzak.infrastructure.Document;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pl.com.sebastianbodzak.domain.ProductType.COSMETICS;

/**
 * Created by Dell on 2016-08-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentFactoryTest {

    private static final String NAME = "test name";
    private static final String NAME_2 = "another test name";
    private static final String PARSED_RECEIPT_CONTENT = "test name 15.75 EUR\nanother test name 15.75 EUR\n\n Total netto sum: 10.00 EUR\n" +
            " Total brutto sum: 12.00 EUR\n\nThu Jan 01 01:00:00 CET 1970\n";
    private static final Money NETTO_PRICE = new Money(15);
    private static final Money BRUTTO_PRICE_GB_COSMETICS_TAX = new Money(15.75);
    private static final ProductType TYPE = COSMETICS;
    private static final TaxPolicy POLICY = new GBStrategy();
    private static final Date DATE = new Date(1l);

    @Mock
    private BarCode barCode;

    @Mock
    private ReceiptNumber receiptNumber;

    private Receipt receipt;
    private Product realProduct;
    private Product realProduct2;

    @Before
    public void setUp() {
        realProduct = new Product(new BarCode("test code"), NAME, NETTO_PRICE, TYPE, POLICY);
        realProduct2 = new Product(new BarCode("test code 2"), NAME_2, NETTO_PRICE, TYPE, POLICY);
        receipt = new Receipt(1, new ReceiptNumber(), true, new Money(10), new Money(12), Arrays.asList(realProduct, realProduct2), DATE);
    }

    @Test
    public void shouldTransformReceiptInformationIntoString() {
        Document document = DocumentFactory.createFrom(receipt);

        assertEquals(PARSED_RECEIPT_CONTENT, document.getContent());
    }

    @Test
    public void shouldPrepareAllDocumentFields() {
        Document document = DocumentFactory.createFrom(receipt);

        assertNotNull(document.getHeadLine());
        assertNotNull(document.getShopName());
        assertNotNull(document.getContent());
        assertNotNull(document.getFooter());
    }
}