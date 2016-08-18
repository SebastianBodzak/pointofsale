package pl.com.sebastianbodzak.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.infrastructure.Display;
import pl.com.sebastianbodzak.infrastructure.Document;
import pl.com.sebastianbodzak.infrastructure.Printer;
import pl.com.sebastianbodzak.infrastructure.Scanner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;
import static pl.com.sebastianbodzak.domain.ProductType.COSMETICS;

/**
 * Created by Dell on 2016-08-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseProcessTest {

    private static final Long EPS = 2L * 1000L;

    private static final String CODE = "testCode";
    private static final String INVALID_CODE_MESSAGE = "Invalid bar-code";
    private static final String ABORTED_PURCHASE_MESSAGE = "Transaction has been aborted";
    private static final String INVALID_PURCHASE_CONFIRM_MESSAGE = "Transaction has been aborted because no product bought";
    private static final String PRODUCT_NOT_FOUND_CODE_MESSAGE = "Product not found";
    private static final Money TOTAL_NETTO_SUM = new Money(10);
    private static final Money TOTAL_BRUTTO_SUM = new Money(12);
    private static final Product PRODUCT = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);
    private static final Receipt RECEIPT = new Receipt(1, new ReceiptNumber(), false, TOTAL_NETTO_SUM, TOTAL_BRUTTO_SUM, Arrays.asList(PRODUCT), new Date());

    private PurchaseProcess purchaseProcess;

    @Mock
    private Scanner scanner;

    @Mock
    private Display display;

    @Mock
    private Printer printer;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private ReceiptNumber anyReceiptNumber;

    @Mock
    private Receipt anyReceipt;

    @Before
    public void setUp() {
        purchaseProcess = new PurchaseProcess(scanner, display, printer, productRepository, receiptRepository);
    }

    @Test
    public void shouldCreateNewReceipt() {

        ReceiptNumber receiptNumber = purchaseProcess.createNewReceipt();

        assertNotNull(receiptNumber);
    }

    @Test
    public void shouldNotAddProductBecauseOfWrongManuallyType() {
        PurchaseResultDto result = purchaseProcess.addProductManually(anyReceiptNumber, null);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotAddProductManuallyBecuaseProductNotFound() throws IOException {
        when(productRepository.load(new BarCode(CODE))).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProductManually(anyReceiptNumber, CODE);

        assertFalse(result.isSuccess());
        assertEquals(PRODUCT_NOT_FOUND_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldAddProductNumberManually() throws Exception {
        Receipt receipt = new Receipt(9, anyReceiptNumber, false, new Money(0), new Money(0), new LinkedList<>(), null);
        Product product = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);

        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(receipt);
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProductManually(anyReceiptNumber, CODE);

        assertTrue(result.isSuccess());
        assertEquals(1, receipt.getProducts().size());
        assertEquals(TOTAL_NETTO_SUM, receipt.getTotalNettoSum());
        assertEquals(TOTAL_BRUTTO_SUM, receipt.getTotalBruttoSum());
    }

    @Test
    public void shouldAddAnotherProductNumberManually() throws Exception {
        Product product = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);
        Receipt receipt = new Receipt(9, anyReceiptNumber, false, TOTAL_NETTO_SUM, TOTAL_BRUTTO_SUM, new LinkedList<>(Arrays.asList(product)), null);

        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(receipt);
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProductManually(anyReceiptNumber, CODE);

        assertTrue(result.isSuccess());
        assertEquals(2, receipt.getProducts().size());
        assertEquals(TOTAL_NETTO_SUM.multiply(2), receipt.getTotalNettoSum());
        assertEquals(TOTAL_BRUTTO_SUM.multiply(2), receipt.getTotalBruttoSum());
    }

    @Test
    public void shouldNotAddProductBecauseOfInvalidScan() throws IOException {
        when(scanner.getProductNumber()).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProduct(anyReceiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotAddProductBecauseProductNotFound() throws IOException {
        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProduct(anyReceiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(PRODUCT_NOT_FOUND_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldAddProductNumber() throws Exception {
        Receipt receipt = new Receipt(9, anyReceiptNumber, false, new Money(0), new Money(0), new LinkedList<>(), null);
        Product product = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);

        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(receipt);
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProduct(anyReceiptNumber);

        assertTrue(result.isSuccess());
        assertEquals(1, receipt.getProducts().size());
        assertEquals(TOTAL_NETTO_SUM, receipt.getTotalNettoSum());
        assertEquals(TOTAL_BRUTTO_SUM, receipt.getTotalBruttoSum());
    }

    @Test
    public void shouldAddAnotherProductNumber() throws Exception {
        Product product = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);
        Receipt receipt = new Receipt(9, anyReceiptNumber, false, TOTAL_NETTO_SUM, TOTAL_BRUTTO_SUM, new LinkedList<>(Arrays.asList(product)), null);

        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(receipt);
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProduct(anyReceiptNumber);

        assertTrue(result.isSuccess());
        assertEquals(2, receipt.getProducts().size());
        assertEquals(TOTAL_NETTO_SUM.multiply(2), receipt.getTotalNettoSum());
        assertEquals(TOTAL_BRUTTO_SUM.multiply(2), receipt.getTotalBruttoSum());
    }

    @Test
    public void shouldAbortPurchaseProcess() throws SQLException {
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(anyReceipt);
        doNothing().when(receiptRepository).delete(anyReceipt);

        PurchaseResultDto result = purchaseProcess.abort(anyReceiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(ABORTED_PURCHASE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotConfirmPurchaseProcess() throws SQLException {
        Receipt receipt = new Receipt(9, anyReceiptNumber, false, TOTAL_NETTO_SUM, TOTAL_BRUTTO_SUM, new LinkedList<>(), null);
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(receipt);

        PurchaseResultDto result = purchaseProcess.confirmAndPrint(anyReceiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_PURCHASE_CONFIRM_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldConfirmPurchaseProcess() throws SQLException {
        when(receiptRepository.load(anyReceiptNumber)).thenReturn(RECEIPT);
        doNothing().when(receiptRepository).save(RECEIPT);
        Document document = DocumentFactory.createFrom(RECEIPT);
        doNothing().when(mock(Printer.class)).print(document);

        purchaseProcess.confirmAndPrint(anyReceiptNumber);

        assertEquals(TOTAL_NETTO_SUM, RECEIPT.getTotalNettoSum());
        assertEquals(TOTAL_BRUTTO_SUM, RECEIPT.getTotalBruttoSum());
        assertNotNull(RECEIPT);
        assertTrue(RECEIPT.isClosed());
        assertTrue(Math.abs(new Date().getTime() - RECEIPT.getConfirmationDate().getTime()) < EPS);
        assertTrue(document.getContent().contains(PRODUCT.getName()));
        assertTrue(document.getContent().contains(PRODUCT.getBruttoPrice().toString()));
        assertTrue(document.getContent().contains(RECEIPT.getTotalNettoSum().toString()));
        assertTrue(document.getContent().contains(RECEIPT.getTotalBruttoSum().toString()));
    }
}
