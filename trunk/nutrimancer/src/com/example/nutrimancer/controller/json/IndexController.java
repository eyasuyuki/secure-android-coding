package com.example.nutrimancer.controller.json;

import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.CharUtils;
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

        writer.write(escape(json));
        writer.close();
        
        return null;
    }

    static String escape(String text) {
        if (text == null || text.length() <= 0) return text;
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<text.length(); i++) {
            if (CharUtils.isAscii(text.charAt(i))) {
                buf.append(text.charAt(i));
            } else {
                buf.append(CharUtils.unicodeEscaped(text.charAt(i)));
            }
        }
        return buf.toString();
    }
}
