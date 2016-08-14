package pl.com.sebastianbodzak.domain;

/**
 * Created by Dell on 2016-08-12.
 */
public class TaxRate {

    private static final char DEFAULT_TAX_SYMBOL = '%';
    private int taxValue;
    private char taxSymbol;

    public TaxRate(int taxValue) {
        this.taxValue = taxValue;
        this.taxSymbol = DEFAULT_TAX_SYMBOL;
    }

    public TaxRate(int taxValue, char taxSymbol) {
        this.taxValue = taxValue;
        this.taxSymbol = taxSymbol;
    }

    public static char getDefaultTaxSymbol() {
        return DEFAULT_TAX_SYMBOL;
    }

    public int getTaxValue() {
        return taxValue;
    }

    public char getTaxSymbol() {
        return taxSymbol;
    }

    @Override
    public String toString() {
        return String.valueOf(taxValue) + taxSymbol;
    }
}
