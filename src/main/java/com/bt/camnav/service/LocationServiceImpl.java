package com.bt.camnav.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.camnav.dao.LocationDAO;
import com.bt.camnav.entity.Location;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDAO locationDAO;

    @Override
    public Location save(Location location) {
        return locationDAO.save(location);
    }

    @Override
    public Location update(Location location) {
        return locationDAO.update(location);
    }

    @Override
    public Location get(Long id) {
        return locationDAO.get(id);
    }

    @Override
    public Location get(String name) {
        return locationDAO.get(name);
    }

    @Override
    public void delete(Long id) {
        locationDAO.delete(id);
    }

    @Override
    public void delete(Location location) {
        locationDAO.delete(location);
    }

    @Override
    public int deleteAll() {
        return locationDAO.deleteAll();
    };

    @Override
    public List<Location> list() {
        return locationDAO.list();
    }

    @Override
    public List<Location> list(int first, int size) {
        return locationDAO.list(first, size);
    }

    @Override
    public List<Location> search(String name, int first, int size) {
        return locationDAO.search(name, first, size);
    }

    @Override
    public Long countRow() {
        return locationDAO.countRow();
    }

    @Override
    public List<Location> filter(Double latitude, Double longitude, Double distance, int first, int size) {
        return locationDAO.filter(latitude, longitude, distance, first, size);
    }

}
