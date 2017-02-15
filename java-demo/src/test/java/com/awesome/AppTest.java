package com.awesome;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void shouldNotFail_whenDoingSomethingBad() throws Exception {
        App app = new App();

        app.doAction("doSomethingBad");
    }

}