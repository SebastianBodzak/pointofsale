package pl.com.sebastianbodzak.domain;

/**
 * Created by Dell on 2016-08-12.
 */
public class Tax {

    private Money bruttoAmount;
    private TaxRate taxRate;

    public Tax(Money bruttoAmount, TaxRate taxRate) {
        this.bruttoAmount = bruttoAmount;
        this.taxRate = taxRate;
    }

    public Money getBruttoAmount() {
        return bruttoAmount;
    }

    public TaxRate getTaxRate() {
        return taxRate;
    }
}
