package pl.com.sebastianbodzak.domain;

import java.util.Optional;

/**
 * Created by Dell on 2016-08-04.
 */
public class Product {

    private Integer id;
    private BarCode barCode;
    private String name;
    private Money nettoPrice;
    private Money bruttoPrice;
    private ProductType productType;
//    private int quantity; additional functionality for future
    private TaxPolicy taxPolicy;

    /**
     * Constructor for loading operation from database
     */
    public Product(Integer id, BarCode barCode, String name, Money nettoPrice, Money bruttoPrice, ProductType productType) {
        this.id = id;
        this.barCode = barCode;
        this.name = name;
        this.nettoPrice = nettoPrice;
        this.bruttoPrice = bruttoPrice;
        this.productType = productType;
    }

    /**
     * Constructor for creating new product
     */
    public Product(BarCode barCode, String name, Money nettoPrice, ProductType productType, TaxPolicy taxPolicy) {
        this.barCode = barCode;
        this.name = name;
        this.nettoPrice = nettoPrice;
        this.bruttoPrice = taxPolicy.calculateCost(nettoPrice, productType).getBruttoAmount();
        this.productType = productType;
    }

    public String getName() {
        return name;
    }

    public Money getNettoPrice() {
        return nettoPrice;
    }

    public Money getBruttoPrice() {
        return bruttoPrice;
    }

    public ProductType getProductType() {
        return productType;
    }

    public BarCode getBarCode() {
        return barCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return barCode != null ? barCode.equals(product.barCode) : product.barCode == null;

    }

    @Override
    public int hashCode() {
        return barCode != null ? barCode.hashCode() : 0;
    }

    public TaxPolicy getTaxPolicy() {
        return taxPolicy;
    }

    public Integer getId() {
        return id;
    }
}
