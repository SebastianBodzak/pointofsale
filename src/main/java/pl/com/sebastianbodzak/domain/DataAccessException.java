package pl.com.sebastianbodzak.domain;

/**
 * Created by Dell on 2016-08-04.
 */
public class DataAccessException extends RuntimeException {

    private BarCode barCode;
    private Class clazz;

    public DataAccessException(String message, BarCode barCode, Class clazz) {
        super(message);
        this.barCode = barCode;
        this.clazz = clazz;
    }

    public DataAccessException(String message, Class clazz) {
        super(message);
        this.clazz = clazz;
    }
}
