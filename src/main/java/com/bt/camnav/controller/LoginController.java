package com.bt.camnav.controller;
 
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
 
@Controller
public class LoginController {

    @RequestMapping(value = "/ajax/login/{key}", method = RequestMethod.GET)
    public ModelAndView ajaxLogin(@PathVariable String key, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("ajax");
        JsonObject json = new JsonObject();
        boolean loggedIn = false;
        if (key != null && !key.trim().isEmpty()) {
            if (key.equals("isLoggedIn")) {
                if (principal != null) {
                    loggedIn = true;
                    String name = principal.getName();
                    json.addProperty("username", name);
                }
            }
        }
        json.addProperty("isLoggedIn", loggedIn);
        modelAndView.addObject("response", json.toString());
        return modelAndView;
    }
 
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
 
		return "login";
 
	}
	
	@RequestMapping(value="/loginfailed", method = RequestMethod.GET)
	public ModelAndView loginError(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("ajax");
        JsonObject json = new JsonObject();

        json.addProperty("isLoggedIn", false);
        
        Object errObj = (Object) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        
        if (errObj instanceof Exception) {
            String error = ((Exception) errObj).getMessage();
            String messageFormat = "Your login attempt was not successful, try again.<br /> Caused : %s";
            String message = String.format(messageFormat, error);
            json.addProperty("message", message);
        }
        
        modelAndView.addObject("response", json.toString());
        
        return modelAndView;
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
 
		return "index";
 
	}
	
}