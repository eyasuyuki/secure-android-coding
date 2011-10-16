package com.example.account.manager.client.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class FoodLog {
    public static final SimpleDateFormat DF =
        new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DTF =
        new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss z");
    public static final String KEY_KEY = "key";
    public static final String VERSION_KEY = "version";
    public static final String USER_KEY = "user";
    public static final String LOG_DATE_KEY = "logDate";
    public static final String TIME_KEY = "time";
    public static final String FOOD_KEY = "food";
    public static final String KCAL_KEY = "kcal";
    public static final String SALT_KEY = "salt";
    public static final String CREATE_DATE_KEY = "createDate";
    private long id = 0L;
    private String key = null;
    private long version = 0L;
    private String user = null;
    private String logDate = null;
    private String time = null;
    private String food = null;
    private double kcal = 0.0;
    private double salt = 0.0;
    private String createDate = null;

    public FoodLog() {
    }

    public FoodLog(JSONObject json) {
        try {
            key = json.getString(KEY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            version = json.getLong(VERSION_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            user = json.getString(USER_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            long ltime = json.getLong(LOG_DATE_KEY);
            logDate = DF.format(new Date(ltime));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            time = json.getString(TIME_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            food = json.getString(FOOD_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            kcal = json.getDouble(KCAL_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            salt = json.getDouble(SALT_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            long ctime = json.getLong(CREATE_DATE_KEY);
            createDate = DTF.format(new Date(ctime));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
