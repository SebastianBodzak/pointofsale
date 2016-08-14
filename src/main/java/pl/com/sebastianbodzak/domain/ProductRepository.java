package pl.com.sebastianbodzak.domain;

/**
 * Created by Dell on 2016-08-08.
 */
public interface ProductRepository {

        Product load(BarCode barCode);
        void save(Product product);
}
