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

    private static final String CODE = "testCode";
    private static final String INVALID_CODE_MESSAGE = "Invalid bar-code";
    private static final String ABORTED_PURCHASE_MESSAGE = "Transaction has been aborted";
    private static final String INVALID_PURCHASE_CONFIRM_MESSAGE = "Transaction has been aborted because no product bought";
    private static final String PRODUCT_NOT_FOUND_CODE_MESSAGE = "Product not found";
    private static final Product PRODUCT = new Product(1, new BarCode(CODE), "testname", new Money(10), new Money(12), COSMETICS);
    private static final Receipt RECEIPT = new Receipt(1, new ReceiptNumber(), false, new Money(10), new Money(12), Arrays.asList(PRODUCT), new Date());

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
    private ReceiptNumber receiptNumber;

    @Mock
    private Receipt receipt;

    @Mock
    private Product product;

    @Mock
    private Money money;


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
        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber, null);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotAddProductManuallyBecuaseProductNotFound() throws IOException {
        when(productRepository.load(new BarCode(CODE))).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber, CODE);

        assertFalse(result.isSuccess());
        assertEquals(PRODUCT_NOT_FOUND_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldAddProductNumberWriteManually() throws Exception {
        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(receiptNumber)).thenReturn(receipt);
        when(product.getName()).thenReturn("");
        when(product.getBruttoPrice()).thenReturn(money);
        when(product.getBruttoPrice().toString()).thenReturn("");
        when(receipt.getTotalBruttoSum()).thenReturn(money);
        when(receipt.getTotalBruttoSum().toString()).thenReturn("");
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber, CODE);

        assertTrue(result.isSuccess());
    }

    @Test
    public void shouldNotAddProductBecauseOfInvalidScan() throws IOException {
        when(scanner.getProductNumber()).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotAddProductBecauseProductNotFound() throws IOException {
        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(null);

        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(PRODUCT_NOT_FOUND_CODE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldAddProductNumber() throws Exception {
        when(scanner.getProductNumber()).thenReturn(CODE);
        when(productRepository.load(new BarCode(CODE))).thenReturn(product);
        when(receiptRepository.load(receiptNumber)).thenReturn(receipt);
        when(product.getName()).thenReturn("");
        when(product.getBruttoPrice()).thenReturn(money);
        when(product.getBruttoPrice().toString()).thenReturn("");
        when(receipt.getTotalBruttoSum()).thenReturn(money);
        when(receipt.getTotalBruttoSum().toString()).thenReturn("");
        doNothing().when(display).show("");

        PurchaseResultDto result = purchaseProcess.addProduct(receiptNumber);

        assertTrue(result.isSuccess());
    }

    @Test
    public void shouldAbortPurchaseProcess() throws SQLException {
        when(receiptRepository.load(receiptNumber)).thenReturn(receipt);
        doNothing().when(receiptRepository).delete(receipt);

        PurchaseResultDto result = purchaseProcess.abort(receiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(ABORTED_PURCHASE_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldNotConfirmPurchaseProcess() throws SQLException {
        when(receiptRepository.load(receiptNumber)).thenReturn(receipt);

        PurchaseResultDto result = purchaseProcess.confirmAndPrint(receiptNumber);

        assertFalse(result.isSuccess());
        assertEquals(INVALID_PURCHASE_CONFIRM_MESSAGE, result.getFailureReason());
    }

    @Test
    public void shouldConfirmPurchaseProcess() throws SQLException {
        when(receiptRepository.load(receiptNumber)).thenReturn(RECEIPT);
        RECEIPT.confirm();
        doNothing().when(receiptRepository).save(RECEIPT);
        Document document = DocumentFactory.createFrom(RECEIPT);
        doNothing().when(printer).print(document);

        boolean containsNameOfProduct = document.getContent().contains(PRODUCT.getName());
        boolean containsPriceOfProduct = document.getContent().contains(PRODUCT.getBruttoPrice().toString());
        boolean containsTotalNettoPrice = document.getContent().contains(RECEIPT.getTotalNettoSum().toString());
        boolean containsTotalBruttoPrice = document.getContent().contains(RECEIPT.getTotalBruttoSum().toString());
        assertNotNull(RECEIPT);
        assertTrue(RECEIPT.isClosed());
        assertNotNull(RECEIPT.getConfirmationDate());
        assertTrue(containsNameOfProduct);
        assertTrue(containsPriceOfProduct);
        assertTrue(containsTotalNettoPrice);
        assertTrue(containsTotalBruttoPrice);
    }
}
