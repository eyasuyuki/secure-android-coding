package com.example.nutrimancer.service;

import java.util.List;
import java.util.Map;

import org.slim3.datastore.Datastore;
import org.slim3.util.BeanUtil;

import com.example.nutrimancer.meta.FoodLogMeta;
import com.example.nutrimancer.model.FoodLog;
import com.google.appengine.api.datastore.Transaction;

public class EntryService {
    
    FoodLogMeta t = new FoodLogMeta();
    
    public FoodLog entry(Map<String, Object> input) {
        FoodLog foodLog = new FoodLog();
        BeanUtil.copy(input, foodLog);
        Transaction tx = Datastore.beginTransaction();
        Datastore.put(foodLog);
        tx.commit();
        return foodLog;
    }
    
    public List<FoodLog> getFoodLogList() {
        return Datastore.query(t).sort(t.createDate.desc).asList();
    }

}
