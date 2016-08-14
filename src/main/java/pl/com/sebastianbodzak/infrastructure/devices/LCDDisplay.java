package pl.com.sebastianbodzak.infrastructure.devices;

import pl.com.sebastianbodzak.infrastructure.Display;

/**
 * Created by Dell on 2016-08-04.
 */
public class LCDDisplay implements Display {

    @Override
    public void show(String information) {
        simulateClearConsole();
        System.out.println(information + "\n");
    }

    private void simulateClearConsole() {
        for (int counter = 0; counter < 5; counter++)
            System.out.println();
        System.out.println("<<<Clear LCD screen simulation>>>\n\n");
    }
}
