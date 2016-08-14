package pl.com.sebastianbodzak.infrastructure.devices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;
import pl.com.sebastianbodzak.infrastructure.Document;

import static pl.com.sebastianbodzak.domain.ProductType.COSMETICS;

/**
 * Created by Dell on 2016-08-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class HPShopPrinter2520Test {

    private static final Money NETTO_PRICE = new Money(15);
    private static final ProductType TYPE = COSMETICS;
    private static final String NAME = "test name";
    private static final TaxPolicy POLICY = new GBStrategy();

    private Receipt receipt;
    private Product realProduct;
    private Product realProduct2;
    private HPShopPrinter2520 printer;

    @Mock
    private BarCode barCode;

    @Mock
    private ReceiptNumber receiptNumber;

    @Before
    public void setUp() {
        printer = new HPShopPrinter2520();
        realProduct = new Product(new BarCode("test code"), NAME, NETTO_PRICE, TYPE, POLICY);
        realProduct2 = new Product(new BarCode("test code 2"), NAME, NETTO_PRICE, TYPE, POLICY);
        receipt = new Receipt();
        receipt.add(realProduct);
        receipt.add(realProduct2);
        receipt.confirm();
    }

    @Test
    public void shouldPrintCorrectDocument() {
        Document document = DocumentFactory.createFrom(receipt);

        printer.print(document);
    }
}
