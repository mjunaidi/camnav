package com.bt.camnav.service;

import java.util.List;

import com.bt.camnav.entity.Location;

public interface LocationService extends GenericService<Location> {
    public List<Location> filter(Double latitude, Double longitude, Double distance, int first, int size);
}
