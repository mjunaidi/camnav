package com.bt.camnav.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LinkController {

    @RequestMapping(value = "/")
    public ModelAndView mainPage(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("index");
        if (principal != null) {
            String name = principal.getName();
            modelAndView.addObject("username", name);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/index")
    public ModelAndView indexPage() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/home")
    public ModelAndView homePage() {
        return new ModelAndView("home");
    }

}
