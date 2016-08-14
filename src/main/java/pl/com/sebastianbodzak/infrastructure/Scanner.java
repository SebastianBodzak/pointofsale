package pl.com.sebastianbodzak.infrastructure;

import java.io.IOException;

/**
 * Created by Dell on 2016-08-04.
 */
public interface Scanner {

    String getProductNumber() throws IOException;
}
