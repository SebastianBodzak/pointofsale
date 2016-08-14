package pl.com.sebastianbodzak.domain;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Random;

import static org.junit.Assert.*;
import static pl.com.sebastianbodzak.domain.Money.CurrencyValues.*;

/**
 * Created by Dell on 2016-08-04.
 */
public class MoneyTest {

    private static final double DOUBLE_WITH_THREE_DECIMALS = 23.456;
    private static final Money.CurrencyValues CURRENCY_WITH_THREE_DECIMALS = TND;
    private static final Money MONEY_AMOUNT = new Money(5.50);
    private static final int PERCENTAGE = 22;
    private static final Money MONEY_22_PERCENTAGE_MULTIPLY = new Money(6.71);
    private static final Money MONEY_AMOUNT2 = new Money(2.55);
    private static final Money MONEY_AMOUNT_WITH_AUD_CURRENCY = new Money(2, AUD);
    private static final Money MONEY_SUM = new Money(8.05);
    private static final Money MONEY_DIFFERENCE = new Money(2.95);
    private static final Double DOUBLE_LESS_THAN_ZERO = -1.11;

    @Test
    public void shouldCreateMoneyWithDoubleType() {
        Double value = getRandomDoubleWithTwoDecimals();
        BigDecimal bigDecimal = useTwoDecimalFormat(value);

        Money money = new Money(value, GBP);

        assertEquals(bigDecimal, money.getValue());
        assertEquals(parseCurrencyValues(GBP), money.getCurrency());
    }

    @Test
    public void shouldCreateMoneyWithDoubleTypeAndDefaultCurrency() {
        Double value = getRandomDoubleWithTwoDecimals();
        BigDecimal bigDecimal = useTwoDecimalFormat(value);

        Money money = new Money(value);

        assertEquals(bigDecimal, money.getValue());
        assertEquals(parseCurrencyValues(EUR), money.getCurrency());
    }

    @Test
    public void shouldCreateMoneyWithInt() {
        int value = getRandomInt();
        BigDecimal bigDecimal = useTwoDecimalFormat(value);

        Money money = new Money(value, CAD);

        assertEquals(bigDecimal, money.getValue());
        assertEquals(parseCurrencyValues(CAD), money.getCurrency());
    }

    @Test
    public void shouldCreateMoneyWithIntAndDefaultCurrency() {
        int value = getRandomInt();
        BigDecimal bigDecimal = useTwoDecimalFormat(value);

        Money money = new Money(value);

        assertEquals(bigDecimal, money.getValue());
        assertEquals(parseCurrencyValues(EUR), money.getCurrency());
    }

    @Test
    public void shouldNotCreateMoneyBecauseOfTwoLongDecimals() {
        try {
            new Money(DOUBLE_WITH_THREE_DECIMALS);
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void shouldNotCreatedMoneyBecauseArgumentIsLessThanZero() {
        try {
            new Money(DOUBLE_LESS_THAN_ZERO);
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void shouldCreateMoneyWithThreeDecimalsAndCorrectCurrency() {
        Money money = new Money(DOUBLE_WITH_THREE_DECIMALS, CURRENCY_WITH_THREE_DECIMALS);

        assertEquals(useThreeDecimalFormat(DOUBLE_WITH_THREE_DECIMALS), money.getValue());
        assertEquals(CURRENCY_WITH_THREE_DECIMALS.toString(), money.getCurrency().getCurrencyCode());
    }

    @Test
    public void shouldCheckIfMoneyIsEqual() {
        Money money = MONEY_AMOUNT;
        Money money1 = MONEY_AMOUNT;

        boolean equal = money.equals(money1);

        assertEquals(true, equal);
        assertEquals(money.getValue(), money1.getValue());
        assertEquals(money.getCurrency(), money1.getCurrency());
    }

    @Test
    public void shouldAddMoney() {
        Money money = MONEY_AMOUNT;
        Money money1 = MONEY_AMOUNT2;
        Money sum = MONEY_SUM;

        Money result = money.add(money1);

        assertEquals(sum, result);
    }

    @Test
    public void shouldNotAddMoneyBecauseOfDifferentCurrency() {
        Money money = MONEY_AMOUNT;
        Money money2 = MONEY_AMOUNT_WITH_AUD_CURRENCY;

        try {
            money.add(money2);
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    @Test
    public void shouldSubtractMoney() {
        Money money = MONEY_AMOUNT;
        Money money1 = MONEY_AMOUNT2;
        Money difference = MONEY_DIFFERENCE;

        Money result = money.subtract(money1);

        assertEquals(difference, result);
    }

    @Test
    public void shouldMultiplyByPercent() {
        Money money = MONEY_AMOUNT;
        int percentage = PERCENTAGE;

        Money result = money.multiplyByPercent(percentage);

        assertEquals(MONEY_22_PERCENTAGE_MULTIPLY, result);
    }

    private double getRandomDoubleWithTwoDecimals() {
        Random random = new Random();
        Double randomDouble = random.nextDouble();
        return Math.round(randomDouble * 10000) / 100.0;
    }

    private BigDecimal useThreeDecimalFormat(Double value) {
            int decimalPlaces = 3;
            BigDecimal bd = new BigDecimal(value);
            return bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal useTwoDecimalFormat(Double value) {
        int decimalPlaces = 2;
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal useTwoDecimalFormat(int value) {
        int decimalPlaces = 2;
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(decimalPlaces);
    }

    private Currency parseCurrencyValues(Money.CurrencyValues currency) {
        return Currency.getInstance(String.valueOf(currency));
    }

    private int getRandomInt() {
        Random random = new Random();
        return Math.abs(random.nextInt());
    }
}
