package pl.com.sebastianbodzak.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static pl.com.sebastianbodzak.domain.Money.CurrencyValues.*;

/**
 * Created by Dell on 2016-08-04.
 *
 * The {@code Money} class represents money. Accept int and double as the parameters.
 */
public class Money {

    /**
     * TND and JOD are 3 decimals currencies
     */
    public enum CurrencyValues {
        AUD, BRL, CAD, CHF, CNH, CZK, DKK, EUR, GBP, HKD, HUF, ILS, INR, JOD, JPY, KRW, MXN, MYR, NOK, NZD, PLN, RUB, SEK, SGD, THB, TND, TRY, TWD, USD, ZAR;

    }

    private static final BigDecimal ONE_HUNDRETH = new BigDecimal(100);
    private static final int ZERO = 0;
    private static final CurrencyValues DEFAULT_CURRENCY = EUR;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private BigDecimal value;

    private Currency currency;
    public Money(Double value, CurrencyValues currency) throws IllegalArgumentException {
        checkIfValueIsCorrect(value, currency);
        this.value = useDecimalFormat(value);
        this.currency = parseCurrencyValues(currency);
    }

    public Money(int value, CurrencyValues currency) throws IllegalArgumentException {
        checkIfValueIsCorrect(value);
        this.value = useDecimalFormat(value, currency);
        this.currency = parseCurrencyValues(currency);
    }

    public Money(Double value) throws IllegalArgumentException {
        this(value, DEFAULT_CURRENCY);
    }

    public Money(int value) throws IllegalArgumentException {
        this(value, DEFAULT_CURRENCY);
    }

    private Money(BigDecimal value, Currency currency) {
        this.value = useDecimalFormat(value.doubleValue());
        this.currency = currency;
    }

    private Currency parseCurrencyValues(CurrencyValues currency) {
        return Currency.getInstance(String.valueOf(currency));
    }

    private BigDecimal useDecimalFormat(Double value) {
        String decimals = getDecimals(String.valueOf(value));
        if (decimals.length() == 3) {
            int decimalPlaces = 3;
            BigDecimal bd = new BigDecimal(value);
            return bd.setScale(decimalPlaces, DEFAULT_ROUNDING);
        } else {
            int decimalPlaces = 2;
            BigDecimal bd = new BigDecimal(value);
            return bd.setScale(decimalPlaces, DEFAULT_ROUNDING);
        }
    }

    private BigDecimal useDecimalFormat(int value, CurrencyValues currency) {
        if (checkIfThreeDecimalCurrency(currency)) {
            int decimalPlaces = 3;
            BigDecimal bd = new BigDecimal(value);
            return bd.setScale(decimalPlaces, DEFAULT_ROUNDING);
        } else {
            int decimalPlaces = 2;
            BigDecimal bd = new BigDecimal(value);
            return bd.setScale(decimalPlaces, DEFAULT_ROUNDING);
        }
    }

    private static boolean checkIfThreeDecimalCurrency(CurrencyValues currency) {
        return currency.equals(TND) || currency.equals(JOD);
    }

    private void checkIfValueIsCorrect(Double value, CurrencyValues currency) throws IllegalArgumentException {
        if (value < ZERO)
            throw new IllegalArgumentException("Amount can not be less than zero!");
        if (valueHasWrongDecimalsQuantity(value, currency))
            throw new IllegalArgumentException("You have typed amount with wrong decimal number");
    }

    private void checkIfValueIsCorrect(int value) throws IllegalArgumentException {
        if (value < ZERO)
            throw new IllegalArgumentException("Amount can not be less than zero");
    }

    /**
     * TND and JOD are 3 decimals currencies
     * @param value
     * @param currency
     * @return
     */
    private boolean valueHasWrongDecimalsQuantity(Double value, CurrencyValues currency) {
        String decimals = String.valueOf(value);
        decimals = getDecimals(decimals);
        return decimals.length() > 2 && !(decimals.length() == 3 && (checkIfThreeDecimalCurrency(currency)));
    }

    private String getDecimals(String value) {
        return value.substring(value.indexOf(".") + 1);
    }

    public Money add(Money amount) throws IllegalStateException {
        checkIfCurrencyAreEqual(currency, amount.getCurrency());
        return new Money(value.add(amount.getValue()), currency);
    }

    public Money subtract(Money amount) throws IllegalStateException {
        checkIfCurrencyAreEqual(currency, amount.getCurrency());
        return new Money(value.subtract(amount.getValue()), currency);
    }

    public Money multiplyByPercent(int percentage) {
        return new Money(value.add(value.multiply(new BigDecimal(percentage)).divide(ONE_HUNDRETH)), currency);
    }

    public Money multiply(int quantity) {
        return new Money(value.multiply(new BigDecimal(quantity)), currency);
    }

    private void checkIfCurrencyAreEqual(Currency currency, Currency currency1) throws IllegalStateException{
        if (!currency.equals(currency1))
            throw new IllegalStateException("Currency mismatch");
    }

    public BigDecimal getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Money parseCentsIntoMoney(int cents, CurrencyValues currency) {
        if (checkIfThreeDecimalCurrency(currency))
            return new Money((double) cents/1000, currency);
        else
            return new Money((double) cents/100, currency);
    }

    public int parseMoneyIntoCents() {
        BigDecimal result;
        if (checkIfThreeDecimalCurrency(CurrencyValues.valueOf(currency.getCurrencyCode())))
            result = value.multiply(new BigDecimal(1000));
        else
            result = value.multiply(new BigDecimal(100));
        return result.intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        if (value != null ? !value.equals(money.value) : money.value != null) return false;
        return currency != null ? currency.equals(money.currency) : money.currency == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value.toString() + " " + currency.getCurrencyCode().toString();
    }
}
