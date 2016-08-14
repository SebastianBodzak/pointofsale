package pl.com.sebastianbodzak.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dell on 2016-08-04.
 */
public class Receipt {

    private Integer id;
    private ReceiptNumber receiptNumber;
    private List<Product> products = new LinkedList<>();
    private boolean closed;
    private Money totalNettoSum;
    private Money totalBruttoSum;
    private Date confirmationDate;

    /**
     * Constructor for creating new Receipt()
     */
    public Receipt() {
        this.receiptNumber = new ReceiptNumber();
        this.closed = false;
        this.totalNettoSum = new Money(0);
        this.totalBruttoSum = new Money(0);
    }

    /**
     * Constructor for loading operation from database
     */
    public Receipt(Integer id, ReceiptNumber receiptNumber, boolean closed, Money totalNettoSum, Money totalBruttoSum, List<Product> products, Date confirmationDate) {
        this.id = id;
        this.receiptNumber = receiptNumber;
        this.closed = closed;
        this.totalNettoSum = totalNettoSum;
        this.totalBruttoSum = totalBruttoSum;
        this.products = products;
        this.confirmationDate = confirmationDate;
    }

    public void add(Product product) throws IllegalStateException {
        verifyReceiptCorrectness();
        addProductAndPrice(product);
    }

    private void addProductAndPrice(Product product) {
        products.add(product);
        addNettoPrice(product);
        addBruttoPrice(product);
    }

    private void addNettoPrice(Product product) {
        totalNettoSum = totalNettoSum.add(product.getNettoPrice());
    }

    private void addBruttoPrice(Product product) {
            totalBruttoSum = totalBruttoSum.add(product.getBruttoPrice());
    }

    private void verifyReceiptCorrectness() throws  IllegalStateException {
        checkIsClosed();
    }

    private void checkIsClosed() {
        if (closed)
            throw new IllegalStateException("Receipt has been already closed");
    }

    public void remove(Product product) throws IllegalArgumentException {
        checkIfProductExists(product);
        products.remove(product);
    }

    private void checkIfProductExists(Product product) throws IllegalArgumentException {
        for (Product prod : products) {
            if (product.equals(prod))
                return;
        }
        throw new IllegalArgumentException("There is not such product");
    }

    public void confirm() {
        closed = true;
        confirmationDate = new Date();
    }

    public List<Product> getProducts() {
        return products;
    }

    public ReceiptNumber getReceiptNumber() {
        return receiptNumber;
    }

    public boolean isClosed() {
        return closed;
    }

    public Money getTotalNettoSum() {
        return totalNettoSum;
    }

    public Money getTotalBruttoSum() {
        return totalBruttoSum;
    }

    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public Product getLastProduct() {
        return products.get(products.size() - 1);
    }

    public Integer getId() {
        return id;
    }
}
