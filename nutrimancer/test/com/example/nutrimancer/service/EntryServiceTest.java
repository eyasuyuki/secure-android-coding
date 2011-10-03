package com.example.nutrimancer.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.AppEngineTestCase;

import com.example.nutrimancer.model.FoodLog;

public class EntryServiceTest extends AppEngineTestCase {
    public static final String USER_KEY = "user";
    public static final String LOG_DATE_KEY = "logDate";
    public static final String TIME_KEY = "time";
    public static final String FOOD_KEY = "food";
    public static final String KCAL_KEY = "kcal";
    public static final String SALT_KEY = "salt";

    public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    
    public static final String USER = "nobody@example.com";
    public static final String LOG_DATE = DF.format(new Date());
    public static final String TIME = "Dinner";
    public static final String FOOD = "Yakisoba";
    public static final double KCAL = 400.0;
    public static final double SALT = 3.0;

    private EntryService service = new EntryService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
    
    @Test
    public void entry() throws Exception {
        Map<String, Object> input = new HashMap<String, Object>();

        input.put(USER_KEY, USER);
        input.put(LOG_DATE_KEY, DF.parseObject(LOG_DATE));
        input.put(TIME_KEY, TIME);
        input.put(FOOD_KEY, FOOD);
        input.put(KCAL_KEY, KCAL);
        input.put(SALT_KEY, SALT);
        FoodLog foodLog = service.entry(input);
        assertThat(foodLog, is(notNullValue()));
        FoodLog stored = Datastore.get(FoodLog.class, foodLog.getKey());
        assertThat(stored.getUser(), is(USER));
        assertThat(stored.getLogDate(), is(DF.parse(LOG_DATE)));
        assertThat(stored.getTime(), is(TIME));
        assertThat(stored.getFood(), is(FOOD));
        assertThat(stored.getKcal(), is(KCAL));
        assertThat(stored.getSalt(), is(SALT));
    }

    @Test
    public void getFoodLogList() throws Exception {
        FoodLog foodLog = new FoodLog();
        foodLog.setUser(USER);
        foodLog.setLogDate(DF.parse(LOG_DATE));
        foodLog.setTime(TIME);
        foodLog.setFood(FOOD);
        foodLog.setKcal(KCAL);
        foodLog.setSalt(SALT);
        Datastore.put(foodLog);
        List<FoodLog> foodLogList = service.getFoodLogList();
        assertThat(foodLogList.size(), is(1));
        assertThat(foodLogList.get(0).getUser(), is(USER));
        assertThat(foodLogList.get(0).getLogDate(), is(DF.parse(LOG_DATE)));
        assertThat(foodLogList.get(0).getTime(), is(TIME));
        assertThat(foodLogList.get(0).getFood(), is(FOOD));
        assertThat(foodLogList.get(0).getKcal(), is(KCAL));
        assertThat(foodLogList.get(0).getSalt(), is(SALT));
    }
}
