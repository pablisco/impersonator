package com.awesome;

import com.evil.EvilClient;

public class App {

    private final EvilClient evilClient = EvilClient.getEvilInstance();

    App() {}

    void doAction(String action) {
        switch (action) {
            case "doSomethingBad":
                evilClient.doSomethingBad();
                break;
        }
    }

    public static void main(String... arguments) {
        App app = new App();

        String action = arguments[0];

        app.doAction(action);
    }

}
