package pl.com.sebastianbodzak.infrastructure.devices;

import pl.com.sebastianbodzak.infrastructure.Document;
import pl.com.sebastianbodzak.infrastructure.Printer;

/**
 * Created by Dell on 2016-08-04.
 */
public class HPShopPrinter2520 implements Printer {

    @Override
    public void print(Document document) {
        System.out.println(document.getShopName()
                + "\n\n" + document.getHeadLine()
                + "\n\n" + document.getContent()
                + "\n" + document.getFooter());
    }
}
