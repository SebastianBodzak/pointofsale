package pl.com.sebastianbodzak.application;

import pl.com.sebastianbodzak.api.PurchaseProcess;
import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;
import pl.com.sebastianbodzak.domain.ProductRepository;
import pl.com.sebastianbodzak.infrastructure.devices.HPShopPrinter2520;
import pl.com.sebastianbodzak.infrastructure.devices.LCDDisplay;
import pl.com.sebastianbodzak.infrastructure.devices.SamsungScanner;
import pl.com.sebastianbodzak.infrastructure.repositories.JDBCCentralReceiptRepository;
import pl.com.sebastianbodzak.infrastructure.repositories.JDBCWareHouseProductRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static pl.com.sebastianbodzak.domain.ProductType.*;

/**
 * GB pointOfSale Simulation (a lot of food articles have not tax rate (same netto and brutto prices))
 */
public class ConsolePointOfSaleApp {
    public static void main(String[] args) throws Exception {
        //preparing simulation******************************************************************************************
        insertProductsToDataBase();
        deleteFileCreatedFormer();
        createFileWithBarCodes();

        final String BARCODES_PATH = "src/tmp/barcodeList.csv";
        final String TEMP_BARCODES_PATH = "src/tmp/tmpbarcodeList.csv";

        Scanner scanner = new Scanner(System.in);

        //point of sale simulation**************************************************************************************
        PurchaseProcess purchaseProcess = new PurchaseProcess(new SamsungScanner(BARCODES_PATH, TEMP_BARCODES_PATH), new LCDDisplay(), new HPShopPrinter2520(),
                new JDBCWareHouseProductRepository("jdbc:hsqldb:hsql://localhost:9001/pointofsale", "SA", ""),
                new JDBCCentralReceiptRepository("jdbc:hsqldb:hsql://localhost:9001/pointofsale", "SA", ""));

        String command;

        showStartSystemInformation(); //information for seller only (on seller's display)
        ReceiptNumber receiptNumber = purchaseProcess.createNewReceipt();

        while(true) {
            showInformationAboutPossibilities(); //information for seller only (on seller's display)
            command = scanCommandBy(scanner);
            if (command.equals("EXIT")) {
                purchaseProcess.confirmAndPrint(receiptNumber);
                break;
            } else if (command.equals("WRITE")) {
                typeManuallyBarCodeInstrunction(); //information for seller only (on seller's display)
                purchaseProcess.addProductManually(receiptNumber, scanner.next());
            } else if (command.equals("ABORT")) {
                purchaseProcess.abort(receiptNumber);
                break;
            } else
                purchaseProcess.addProduct(receiptNumber);
        }
    }

    //utils methods*****************************************************************************************************
    private static void insertProductsToDataBase() {
        TaxPolicy strategy = new GBStrategy();
        ProductRepository repository = new JDBCWareHouseProductRepository("jdbc:hsqldb:hsql://localhost:9001/pointofsale", "SA", "");
        Product product = new Product(new BarCode("test-barcode-0001"), "wheat bread", new Money(1.25), BREAD, strategy);
        Product product2 = new Product(new BarCode("test-barcode-0002"), "apple champion", new Money(0.8), FRUIT, strategy);
        Product product3 = new Product(new BarCode("test-barcode-0003"), "socks puma", new Money(2), CLOTHES, strategy);
        Product product4 = new Product(new BarCode("test-barcode-0004"), "free chicken", new Money(4.40), MEAT, strategy);
        Product product5 = new Product(new BarCode("test-barcode-0005"), "black pepper", new Money(1), SPICES, strategy);
        Product product6 = new Product(new BarCode("test-barcode-0006"), "beer komes", new Money(1.30), ALCOHOL, strategy);
        repository.save(product);
        repository.save(product2);
        repository.save(product3);
        repository.save(product4);
        repository.save(product5);
        repository.save(product6);
    }

    private static void deleteFileCreatedFormer() throws Exception {
        try {
            new File("src/tmp/barcodelist.csv").delete();
        } catch (Exception ex) {
            throw new Exception("Can not delete file");
        }
    }

    private static void createFileWithBarCodes() throws FileNotFoundException {
        FileCreator fileCreator = new FileCreator("src/tmp/barcodelist.csv");
        fileCreator.create("test-barcode-0001", "test-barcode-0002", "test-barcode-0002", "test-barcode-doesNotExists", "test-barcode-0003",
                "test-barcode-0004", "", "test-barcode-0005", "test-barcode-0006");
    }

    private static String scanCommandBy(Scanner scanner) {
        return scanner.next().toUpperCase();
    }

    //<UTILS> information for seller************************************************************************************
    private static void showStartSystemInformation() {
        System.out.println("Purchase process has been initiated");
    }

    private static void showInformationAboutPossibilities() {
        System.out.println("Type EXIT if you want to confirm and print transaction, WRITE if you want type bar-code manually," +
                "ABORT for transaction aborting or anything else for scanning product;" +
                "next confirm your decision by enter");
    }

    private static void typeManuallyBarCodeInstrunction() {
        System.out.println("Type bar-code manually, check its correctness and press enter, please");
    }
}
