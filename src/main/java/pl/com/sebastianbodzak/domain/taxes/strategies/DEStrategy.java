package pl.com.sebastianbodzak.domain.taxes.strategies;

import pl.com.sebastianbodzak.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.com.sebastianbodzak.domain.ProductType.*;

/**
 * Created by Dell on 2016-08-09.
 */
public class DEStrategy implements TaxPolicy{

    private static final TaxRate LOWER_TAX_RATE = new TaxRate(7);
    private static final TaxRate HIGHER_TAX_RATE = new TaxRate(19);

    private List<ProductType> listOfLuxuryProducts = new ArrayList<>(Arrays.asList(VEGETABLE, FRUIT, COSMETICS, CLOTHES, MEAT, SWEETS, FISH, DAIRY_PRODUCTS, DRINKS, ALCOHOL, CIGARETTES, PAPERSTUFF));
    private List<ProductType> listOfCommonProducts = new ArrayList<>(Arrays.asList(BREAD, SPICES));

    @Override
    public Tax calculateCost(Money nettoPrice, ProductType productType) {
        if (listOfCommonProducts.contains(productType))
            return new Tax(nettoPrice.multiplyByPercent(LOWER_TAX_RATE.getTaxValue()), LOWER_TAX_RATE);
        else
            return new Tax(nettoPrice.multiplyByPercent(HIGHER_TAX_RATE.getTaxValue()), HIGHER_TAX_RATE);
    }
}
