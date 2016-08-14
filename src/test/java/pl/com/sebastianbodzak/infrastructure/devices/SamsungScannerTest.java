package pl.com.sebastianbodzak.infrastructure.devices;

import org.junit.Before;
import org.junit.Test;
import pl.com.sebastianbodzak.application.FileCreator;
import pl.com.sebastianbodzak.domain.*;
import pl.com.sebastianbodzak.domain.taxes.strategies.GBStrategy;
import pl.com.sebastianbodzak.infrastructure.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static pl.com.sebastianbodzak.domain.ProductType.BREAD;


/**
 * Created by Dell on 2016-08-10.
 */
public class SamsungScannerTest {

    private static final TaxPolicy STRATEGY = new GBStrategy();
    private static final Product PRODUCT = new Product(new BarCode("testbarcode"), "wheat bread", new Money(1.25), BREAD, STRATEGY);
    private static final String CODE_PATH = "src/test/java/fixtures/testbarcode.csv";
    private static final String TEMP_CODE_PATH = "src/test/java/fixtures/tmptestbarcode.csv";
    private static final String EMPTY_CODE_PATH = "src/test/java/fixtures/emptybarcode.csv";
    private static final String INVALID_PATH = "someinvalidpath.csv";
    private static final String CODE = "testbarcode";
    private static final String CODE2 = "testbarcode2";
    private static final String CODE3 = "testbarcode3";

    @Before
    public void setUp() throws Exception {
        deleteFileCreatedFormer();
        createFileWithBarCodes();
        deleteFileWithEmptyCode();
        createFileWithEmptyCode();
    }

    @Test
    public void shouldReadCode() throws IOException {
        Scanner scanner = new SamsungScanner(CODE_PATH, TEMP_CODE_PATH);

        String code = scanner.getProductNumber();

        assertEquals("testbarcode", code);
    }

    @Test
    public void shouldThrowExceptionWhenPathToFileIsInvalid() {
        Scanner scanner = new SamsungScanner(INVALID_PATH, INVALID_PATH);

        try {
            scanner.getProductNumber();
            fail();
        }
        catch (Exception e) {
        }
    }

    @Test
    public void shouldReadAllCodes() throws IOException {
        Scanner scanner = new SamsungScanner(CODE_PATH, TEMP_CODE_PATH);
        List<String> codes = new LinkedList<>();

        String fileLine;
        while((fileLine = scanner.getProductNumber()) != null) {
            codes.add(fileLine);
        }

        assertEquals(CODE, codes.get(0));
        assertEquals(CODE2, codes.get(1));
        assertEquals(CODE3, codes.get(2));
        assertTrue(codes.size() == 3);
    }

    @Test
    public void shouldReturnNullWhenCodeIsInvalid() throws IOException {
        Scanner scanner = new SamsungScanner(EMPTY_CODE_PATH, TEMP_CODE_PATH);

        String code = scanner.getProductNumber();

        assertNull(code);
    }

    private void deleteFileCreatedFormer() throws Exception {
        try {
            new File(CODE_PATH).delete();
        } catch (Exception ex) {
            throw new Exception("Can not delete file");
        }
    }

    private void createFileWithBarCodes() throws FileNotFoundException {
        FileCreator fileCreator = new FileCreator(CODE_PATH);
        fileCreator.create(CODE, CODE2, CODE3);
    }

    private void deleteFileWithEmptyCode() throws Exception {
        try {
            new File(EMPTY_CODE_PATH).delete();
        } catch (Exception ex) {
            throw new Exception("Can not delete file");
        }
    }

    private void createFileWithEmptyCode() throws FileNotFoundException {
        FileCreator fileCreator = new FileCreator(EMPTY_CODE_PATH);
        fileCreator.create("");
    }
}
