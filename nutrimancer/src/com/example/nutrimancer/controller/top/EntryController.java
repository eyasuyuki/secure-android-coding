package com.example.nutrimancer.controller.top;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.RequestMap;

import com.example.nutrimancer.service.EntryService;

public class EntryController extends Controller {
    
    EntryService service = new EntryService();

    @Override
    public Navigation run() throws Exception {
        service.entry(new RequestMap(request));
        return redirect(basePath);
    }
}
