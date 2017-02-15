package com.evil;

public class EvilClient {

    public static final String AWFUL = "AWFUL";

    public static EvilClient getEvilInstance() {
        return new EvilClient();
    }

    public void doSomethingBad() {
        throw new RuntimeException();
    }

    public String getSomethingBad() {
        return AWFUL;
    }

    public void setSomethingBad(String value) {

    }

    public String doSomethingBad(String value) {
        return null;
    }

}
