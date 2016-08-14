package pl.com.sebastianbodzak.domain;

/**
 * Created by Dell on 2016-08-05.
 */
public interface TaxPolicy {
    Tax calculateCost(Money nettoPrice, ProductType productType);
}
