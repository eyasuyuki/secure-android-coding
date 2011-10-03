package com.example.nutrimancer.controller.top;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.example.nutrimancer.model.FoodLog;
import com.example.nutrimancer.service.EntryServiceTest;

public class EntryControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.param(EntryServiceTest.USER_KEY, EntryServiceTest.USER);
        tester.param(EntryServiceTest.LOG_DATE_KEY, EntryServiceTest.LOG_DATE);
        tester.param(EntryServiceTest.TIME_KEY, EntryServiceTest.TIME);
        tester.param(EntryServiceTest.FOOD_KEY, EntryServiceTest.FOOD);
        tester.param(EntryServiceTest.KCAL_KEY, EntryServiceTest.KCAL);
        tester.param(EntryServiceTest.SALT_KEY, EntryServiceTest.SALT);
        tester.start("/top/entry");
        EntryController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(true));
        assertThat(tester.getDestinationPath(), is("/top/"));
        FoodLog stored = Datastore.query(FoodLog.class).asSingle();
        assertThat(stored, is(notNullValue()));
        assertThat(stored.getUser(), is(EntryServiceTest.USER));
        assertThat(stored.getLogDate(), is(EntryServiceTest.DF.parse(EntryServiceTest.LOG_DATE)));
        assertThat(stored.getTime(), is(EntryServiceTest.TIME));
        assertThat(stored.getFood(), is(EntryServiceTest.FOOD));
        assertThat(stored.getKcal(), is(EntryServiceTest.KCAL));
        assertThat(stored.getSalt(), is(EntryServiceTest.SALT));
    }
}
