package pl.com.sebastianbodzak.application;

import org.junit.After;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dell on 2016-08-10.
 */
public class FileCreatorTest {

    private final static String PATH = "src/tmp/newbarcodelist.csv";
    private final static String BARCODE_1 = "code-1";
    private final static String BARCODE_2 = "code-2";
    private final static String BARCODE_3 = "code-3";
    private final static String THREE_WRITTEN_CODES = "code-1code-2code-3";

    @After
    public void deleteFile() {
        File file = new File(PATH);
        file.delete();
    }

    @Test
    public void shouldCreateNewFile() throws FileNotFoundException {
        FileCreator fc = new FileCreator(PATH);

        fc.create(BARCODE_1);

        File file = fc.getFile();
        assertTrue(file.exists() && !file.isDirectory());
    }

    @Test
    public void shouldWriteFile() throws IOException {
        FileCreator fc = new FileCreator(PATH);

        fc.create(BARCODE_1, BARCODE_2, BARCODE_3);

        String result = readFile(fc.getFile());
        assertEquals(THREE_WRITTEN_CODES, result);
    }

    private String readFile(File file) throws IOException {
        String result = "";
        StringBuilder sb = new StringBuilder(result);
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = bf.readLine()) != null)
                sb.append(currentLine);
        } catch (Exception ex) {
            throw new IOException();
        }
        return sb.toString();
    }
}
