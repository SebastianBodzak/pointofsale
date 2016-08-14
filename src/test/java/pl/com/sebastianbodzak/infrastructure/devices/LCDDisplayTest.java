package pl.com.sebastianbodzak.infrastructure.devices;

import org.junit.Test;
import pl.com.sebastianbodzak.infrastructure.Display;
import java.util.UUID;

/**
 * Created by Dell on 2016-08-11.
 */
public class LCDDisplayTest {

    @Test
    public void shouldPrintGivenInformation() {
        Display display = new LCDDisplay();
        String randomInformation = UUID.randomUUID().toString();

        display.show(randomInformation);
    }
}
