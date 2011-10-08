package com.example.nutrimancer.controller.top;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.example.nutrimancer.model.FoodLog;
import com.example.nutrimancer.service.EntryService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class IndexController extends Controller {

    EntryService service = new EntryService();

    @Override
    public Navigation run() throws Exception {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        String thisURL = request.getRequestURI();
        List<FoodLog> foodLogList = service.getFoodLogList();
        requestScope("foodLogList", foodLogList);
        requestScope("email", user.getEmail());
        requestScope("logoutUrl", userService.createLogoutURL(thisURL));
        return forward("index.jsp");
    }
}
