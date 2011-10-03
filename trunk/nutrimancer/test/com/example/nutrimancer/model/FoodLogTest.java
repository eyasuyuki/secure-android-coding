package com.example.nutrimancer.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class FoodLogTest extends AppEngineTestCase {

    private FoodLog model = new FoodLog();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
