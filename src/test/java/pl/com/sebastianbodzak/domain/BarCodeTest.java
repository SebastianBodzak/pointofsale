package pl.com.sebastianbodzak.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dell on 2016-08-04.
 */
public class BarCodeTest {

    private String code;

    @Before
    public void setUp() {
        code = getRandomString();
    }

    @Test
    public void shouldCreateBarCode() {
        BarCode barCode = new BarCode(code);

        assertEquals(code, barCode.getCode());
    }

    @Test
    public void shouldBarCodesBeEqual() {
        BarCode barCode = new BarCode(code);

        BarCode sameBarCode = new BarCode(code);

        assertTrue(barCode.equals(sameBarCode));
    }

    @Test
    public void shouldNotBarCodesBeEqual() {
        String code2 = getRandomString();
        BarCode barCode1 = new BarCode(code);

        BarCode barCode2 = new BarCode(code2);

        assertFalse(barCode1.equals(barCode2));
    }

    private String getRandomString() {
        return UUID.randomUUID().toString();
    }
}
