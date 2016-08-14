package pl.com.sebastianbodzak.domain;

import org.junit.Test;

/**
 * Created by Dell on 2016-08-11.
 */
public class DataAccessExceptionTest {

    @Test (expected = DataAccessException.class)
    public void shouldThrowException() {
        throw new DataAccessException("Can not access", DataAccessExceptionTest.class);
    }
}
