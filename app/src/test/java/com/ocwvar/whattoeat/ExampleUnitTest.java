package com.ocwvar.whattoeat;

import com.google.gson.Gson;
import com.ocwvar.whattoeat.Unit.Menu;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        final String data = "{\"foods\":[{\"message\":\"\",\"title\":\"您近年来\"}],\"message\":\"\",\"title\":\"积极\"}";
        final Menu menu = new Gson().fromJson(data, Menu.class);
    }
}