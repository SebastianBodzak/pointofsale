package pl.com.sebastianbodzak.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Dell on 2016-08-11.
 */
public class ReceiptNumberTest {

    private static final String TEST_NUMBER = "test-number";

    @Test
    public void shouldCreateReceiptNumber() {
        ReceiptNumber receiptNumber = new ReceiptNumber();

        assertNotNull(receiptNumber.getNumber());
    }

    @Test
    public void shouldCreateReceiptNumberWithGivenNumber() {
        ReceiptNumber receiptNumber = new ReceiptNumber(TEST_NUMBER);

        assertEquals(TEST_NUMBER, receiptNumber.getNumber());
    }
}
