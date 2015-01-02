package com.bt.camnav.controller;

import java.util.List;

import com.bt.camnav.entity.Location;
import com.bt.camnav.service.LocationService;
import com.bt.camnav.util.JsonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Controller
@RequestMapping(value = "/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @RequestMapping(value = "/api/countRow", method = RequestMethod.GET)
    @ResponseBody
    public String countRow() {
        Long rowCount = locationService.countRow();
        JsonObject json = new JsonObject();
        json.addProperty("rowCount", rowCount);
        return json.toString();
    }

    @RequestMapping(value = "/api/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable Long id) {
        Location location = locationService.get(id);
        JsonObject json = (JsonObject) JsonUtil.INSTANCE.jsonize(location);
        return json.toString();
    }

    @RequestMapping(value = "/api/get/name/{name}", method = RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable String name) {
        Location location = locationService.get(name);
        JsonObject json = (JsonObject) JsonUtil.INSTANCE.jsonize(location);
        return json.toString();
    }

    @RequestMapping(value = "/api/list", method = RequestMethod.GET)
    @ResponseBody
    public String list() {
        List<Location> locations = locationService.list();
        JsonArray array = (JsonArray) JsonUtil.INSTANCE.jsonize(locations);
        return array.toString();
    }

    @RequestMapping(value = "/api/list/{first}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public String list(@PathVariable Integer first, @PathVariable Integer size) {
        List<Location> locations = locationService.list(first, size);
        JsonArray array = (JsonArray) JsonUtil.INSTANCE.jsonize(locations);
        return array.toString();
    }

    @RequestMapping(value = "/api/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(@RequestBody String requestBody) {
        JsonElement json = JsonUtil.INSTANCE.parse(requestBody);
        boolean status = false;
        String message = null;
        Long id = null;

        if (json != null) {
            Location location = JsonUtil.INSTANCE.fromJson(json, Location.class);

            if (location != null) {
                location = locationService.save(location);
                status = true;
                id = location.getId();
            }
        }

        if (status) {
            message = "Location created!";
        } else {
            message = "Not able to create location!";
        }

        JsonObject response = JsonUtil.INSTANCE.createSuccessResponse(status, message, id);

        return response.toString();
    }

    @RequestMapping(value = "/api/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String update(@PathVariable Long id, @RequestBody String requestBody) {
        JsonElement json = JsonUtil.INSTANCE.parse(requestBody);
        boolean status = false;
        String message = null;

        if (json != null) {
            Location location = JsonUtil.INSTANCE.fromJson(json, Location.class);
            if (location != null) {
                locationService.update(location);
                status = true;
            }
        }

        if (status) {
            message = "Location updated!";
        } else {
            message = "Not able to update location!";
        }

        JsonObject response = JsonUtil.INSTANCE.createBasicResponse(status, message);

        return response.toString();
    }

    @RequestMapping(value = "/api/remove/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String remove(@PathVariable Long id) {
        boolean status = false;
        String message = null;

        locationService.delete(id);
        message = "The location has been removed!";
        status = true;

        JsonObject response = JsonUtil.INSTANCE.createBasicResponse(status, message);

        return response.toString();
    }

    @RequestMapping(value = "/api/removeAll", method = RequestMethod.GET)
    @ResponseBody
    public String removeAll() {
        boolean status = false;
        StringBuilder message = new StringBuilder(128);

        int count = locationService.deleteAll();
        message.append(count).append(" records deleted!");
        status = true;

        JsonObject response = JsonUtil.INSTANCE.createBasicResponse(status, message.toString());

        return response.toString();
    }

    @RequestMapping(value = "/api/search/{name}/{first}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public String search(@PathVariable String name, @PathVariable Integer first, @PathVariable Integer size) {
        List<Location> locations = locationService.search(name, first, size);

        JsonArray array = (JsonArray) JsonUtil.INSTANCE.jsonize(locations);

        return array.toString();
    }
}