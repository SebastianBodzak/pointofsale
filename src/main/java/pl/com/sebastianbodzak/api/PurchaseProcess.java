package pl.com.sebastianbodzak.api;

import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.infrastructure.*;
import pl.com.sebastianbodzak.infrastructure.Scanner;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Created by Dell on 2016-08-08.
 */
public class PurchaseProcess {

    private Scanner scanner;
    private Display display;
    private Printer printer;
    private ProductRepository productRepository;
    private ReceiptRepository receiptRepository;

    public PurchaseProcess(Scanner scanner, Display display, Printer printer, ProductRepository productRepository, ReceiptRepository receiptRepository) {
        this.scanner = scanner;
        this.display = display;
        this.printer = printer;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
    }

    public ReceiptNumber createNewReceipt() {
        Receipt receipt = new Receipt();
        receiptRepository.save(receipt);
        return receipt.getReceiptNumber();
    }

    public PurchaseResultDto addProductManually(ReceiptNumber receiptNumber, String code){
        if (isInvalid(code)) return failureAndShow("Invalid bar-code");

        BarCode barCode = new BarCode(code);
        Product product = productRepository.load(barCode);
        if (notFound(product)) return failureAndShow("Product not found");

        Receipt receipt = receiptRepository.load(receiptNumber);
        receipt.add(product);
        receiptRepository.save(receipt);
        display.show(prepareInformation(product, receipt));
        return success();
    }

    public PurchaseResultDto addProduct(ReceiptNumber receiptNumber) throws DataAccessException, IllegalArgumentException, IOException {
        String scan = scanner.getProductNumber();
        if (isInvalid(scan)) return failureAndShow("Invalid bar-code");

        BarCode barCode = new BarCode(scan);
        Product product = productRepository.load(barCode);
        if (notFound(product)) return failureAndShow("Product not found");

        Receipt receipt = receiptRepository.load(receiptNumber);
        receipt.add(product);
        receiptRepository.save(receipt);
        display.show(prepareInformation(product, receipt));
        return success();
    }

    private boolean isInvalid(String scan) {
        return scan == null;
    }

    private boolean notFound(Product product) {
        return product == null;
    }

    private PurchaseResultDto success() {
        return new PurchaseResultDto();
    }

    private PurchaseResultDto failureAndShow(String errorMessage) {
        display.show(errorMessage);
        return new PurchaseResultDto(errorMessage);
    }

    private String prepareInformation(Product product, Receipt receipt) {
        return product.getName() + " price: " + product.getBruttoPrice().toString()
                + "\nTotal brutto sum: " + receipt.getTotalBruttoSum().toString();
    }

    public PurchaseResultDto abort(ReceiptNumber receiptNumber) throws SQLException {
        Receipt receipt = receiptRepository.load(receiptNumber);
        receiptRepository.delete(receipt);
        return failureAndShow("Transaction has been aborted");
    }

    public PurchaseResultDto confirmAndPrint(ReceiptNumber receiptNumber) throws SQLException {
        Receipt receipt = receiptRepository.load(receiptNumber);
        if (noProductBought(receipt)) return failureAndShow("Transaction has been aborted because no product bought");
        receipt.confirm();
        receiptRepository.save(receipt);
        Document document = DocumentFactory.createFrom(receipt);
        printer.print(document);
        return success();
    }

    private boolean noProductBought(Receipt receipt) throws SQLException {
        return receipt.getProducts().size() == 0;
    }
}
