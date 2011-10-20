package com.example.nutrimancer.controller.json;

import java.io.Writer;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.ResponseLocator;

import com.example.nutrimancer.meta.FoodLogMeta;
import com.example.nutrimancer.model.FoodLog;
import com.example.nutrimancer.service.EntryService;

public class IndexController extends Controller {

    EntryService service = new EntryService();

    @Override
    public Navigation run() throws Exception {
        List<FoodLog> foodLogList = service.getFoodLogList();
        String json = FoodLogMeta.get().modelsToJson(foodLogList.toArray());
        
        response.setContentType("application/json"); 
        response.setCharacterEncoding("UTF-8"); 
        
        Writer writer = ResponseLocator.get().getWriter();

        writer.write(json);
        writer.close();
        
        return null;
    }
}
