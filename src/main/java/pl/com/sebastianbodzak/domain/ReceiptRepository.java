package pl.com.sebastianbodzak.domain;

import java.sql.SQLException;

/**
 * Created by Dell on 2016-08-08.
 */
public interface ReceiptRepository {

    Receipt load(ReceiptNumber receiptNumber);
    void save(Receipt receipt);
    void delete(Receipt receipt) throws SQLException;
}
