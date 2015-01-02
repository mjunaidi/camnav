package com.bt.camnav.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonElement;

public enum ApiUtil {
    INSTANCE;

    public ModelAndView createAjaxModelAndView(JsonElement json) {
        ModelAndView modelAndView = new ModelAndView("ajax");
        if (json != null) {
            modelAndView.addObject("response", json.toString());
        }
        return modelAndView;
    }

    public List<String> stringToList(String str) {
        if (str != null && !str.isEmpty()) {
            String delim = ",";
            StringTokenizer tokenizer = new StringTokenizer(str, delim);
            int size = tokenizer.countTokens();
            List<String> list = new ArrayList<String>(size);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token != null && !token.trim().isEmpty()) {
                    list.add(token);
                }
            }
            return list;
        }
        return null;
    }
}
