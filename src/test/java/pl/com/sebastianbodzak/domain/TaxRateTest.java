package pl.com.sebastianbodzak.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dell on 2016-08-12.
 */
public class TaxRateTest {

    @Test
    public void shouldCreateTaxRate() {
        TaxRate taxRate = new TaxRate(23);

        assertEquals(23, taxRate.getTaxValue());
        assertEquals('%', taxRate.getTaxSymbol());
    }

    @Test
    public void shouldParseTaxRateToString() {
        TaxRate taxRate = new TaxRate(23);

        String result = taxRate.toString();

        assertEquals("23%", result);
    }
}
