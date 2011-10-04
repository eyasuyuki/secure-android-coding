package com.example.nutrimancer.service;

import java.util.List;
import java.util.Map;

import org.slim3.datastore.Datastore;
import org.slim3.util.BeanUtil;

import com.example.nutrimancer.meta.FoodLogMeta;
import com.example.nutrimancer.model.FoodLog;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class EntryService {
    
    FoodLogMeta t = new FoodLogMeta();
    
    public FoodLog entry(Map<String, Object> input) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        FoodLog foodLog = new FoodLog();
        BeanUtil.copy(input, foodLog);
        if (user != null) {
            foodLog.setUser(user.getUserId());
        }
        Transaction tx = Datastore.beginTransaction();
        Datastore.put(foodLog);
        tx.commit();
        return foodLog;
    }
    
    public List<FoodLog> getFoodLogList() {
        List<FoodLog> result = null;

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            result =
                Datastore.query(t).filter(t.user.equal(user.getUserId())).sort(t.createDate.desc).asList();
        }
        return result;
    }
}
