package pl.com.sebastianbodzak.domain.taxes.strategies;

import pl.com.sebastianbodzak.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.com.sebastianbodzak.domain.ProductType.*;
import static pl.com.sebastianbodzak.domain.ProductType.SPICES;

/**
 * Created by Dell on 2016-08-09.
 */
public class GBStrategy implements TaxPolicy {

    private static final TaxRate LOWER_TAX_RATE = new TaxRate(5);
    private static final TaxRate HIGHER_TAX_RATE = new TaxRate(20);
    private static final TaxRate ZERO_TAX_RATE = new TaxRate(0);

    private List<ProductType> listOfLuxuryProducts = new ArrayList<>(Arrays.asList(ALCOHOL, CIGARETTES));
    private List<ProductType> listOfMediumProducts = new ArrayList<>(Arrays.asList(COSMETICS, CLOTHES));
    private List<ProductType> listOfFreeFromTaxesProducts = new ArrayList<>(Arrays.asList(BREAD, SPICES, VEGETABLE, FRUIT, MEAT, SWEETS, FISH, DAIRY_PRODUCTS, DRINKS, PAPERSTUFF));

    @Override
    public Tax calculateCost(Money nettoPrice, ProductType productType) {
        if (listOfLuxuryProducts.contains(productType))
            return new Tax(nettoPrice.multiplyByPercent(HIGHER_TAX_RATE.getTaxValue()), HIGHER_TAX_RATE);
        else if (listOfMediumProducts.contains(productType))
            return new Tax(nettoPrice.multiplyByPercent(LOWER_TAX_RATE.getTaxValue()), LOWER_TAX_RATE);
        else
            return new Tax(nettoPrice, ZERO_TAX_RATE);
    }
}
