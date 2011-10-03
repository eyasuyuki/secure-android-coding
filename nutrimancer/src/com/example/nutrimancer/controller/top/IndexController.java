package com.example.nutrimancer.controller.top;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.example.nutrimancer.model.FoodLog;
import com.example.nutrimancer.service.EntryService;

public class IndexController extends Controller {

    EntryService service = new EntryService();
    
    @Override
    public Navigation run() throws Exception {
        List<FoodLog> foodLogList = service.getFoodLogList();
        requestScope("foodLogList", foodLogList);
        return forward("index.jsp");
    }
}
