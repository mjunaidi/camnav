package com.bt.camnav.dao;

import java.util.List;

import com.bt.camnav.entity.Location;

public interface LocationDAO extends GenericDAO<Location>{
    public List<Location> filter(Double latitude, Double longitude, Double distance, int first, int size);
}
