package pl.com.sebastianbodzak.domain;

import java.util.UUID;

/**
 * Created by Dell on 2016-08-08.
 */
public class ReceiptNumber {

    private String number;

    public ReceiptNumber() {
        this.number = UUID.randomUUID().toString();
    }

    public ReceiptNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
