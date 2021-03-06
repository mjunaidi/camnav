package com.bt.camnav.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bt.camnav.entity.Location;
import com.bt.camnav.util.Coordinate;
import com.bt.camnav.util.LocationUtil;

@Repository
public class LocationDAOImpl implements LocationDAO {

    public static final int MAX = 1000;

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Location save(Location location) {
        if (location != null) {
            location.setUpdated(new Date());
            getCurrentSession().save(location);
            return location;
        }
        return null;
    }

    public Location update(Location location) {
        if (location != null) {
            Location toUpdate = get(location.getId());
            if (toUpdate != null) {
                toUpdate.setName(location.getName());
                toUpdate.setDescription(location.getDescription());
                toUpdate.setLatitude(location.getLatitude());
                toUpdate.setLongitude(location.getLongitude());
                toUpdate.setUpdated(new Date());
                getCurrentSession().update(toUpdate);
                return toUpdate;
            }
        }
        return null;
    }

    public Location get(Long id) {
        Location location = (Location) getCurrentSession().get(Location.class, id);
        initializeProxies(location);
        return location;
    }

    public Location get(String name) {
        if (name == null)
            return null;
        Location location = (Location) getCurrentSession().createCriteria(Location.class)
                .add(Restrictions.eq("name", name)).uniqueResult();
        initializeProxies(location);
        return location;
    }

    public void delete(Long id) {
        Location location = get(id);
        if (location != null)
            getCurrentSession().delete(location);
    }

    public void delete(Location location) {
        getCurrentSession().delete(location);
    }

    @Override
    public int deleteAll() {
        List<Location> locations = list();
        int count = 0;
        while (locations != null && !locations.isEmpty()) {
            for (Location location : locations) {
                delete(location);
                count++;
            }
            locations = list();
        }
        return count;
    }

    public List<Location> list() {
        return list(0, MAX);
    }

    @SuppressWarnings("unchecked")
    public List<Location> list(int first, int size) {
        if (size > MAX)
            size = MAX;

        Criteria criteria = getCurrentSession().createCriteria(Location.class);
        criteria.setFirstResult(first);
        criteria.setMaxResults(size);

        List<Location> locations = criteria.list();

        for (Location location : locations) {
            initializeProxies(location);
        }

        return locations;
    }

    @SuppressWarnings("unchecked")
    public List<Location> search(String name, int first, int size) {
        if (name == null)
            return null;

        String wildcard = "%";
        String keyword = String.format("%s%s%s", wildcard, name, wildcard);
        
        Criteria criteria = getCurrentSession().createCriteria(Location.class);
        criteria.add(Restrictions.or(Restrictions.ilike("name", keyword),
                Restrictions.ilike("description", keyword)));

        criteria.setFirstResult(first);
        criteria.setMaxResults(size);

        List<Location> locations = criteria.list();

        return locations;
    }

    @SuppressWarnings("unchecked")
    public List<Location> filter(Double latitude, Double longitude, Double distance, int first, int size) {
        if (latitude == null || longitude == null || distance == null)
            return null;

        Criteria criteria = getCurrentSession().createCriteria(Location.class);

        Coordinate upperBound = LocationUtil.INSTANCE.getUpperBound(latitude.doubleValue(), longitude.doubleValue(),
                distance.doubleValue());
        Coordinate lowerBound = LocationUtil.INSTANCE.getLowerBound(latitude.doubleValue(), longitude.doubleValue(),
                distance.doubleValue());

        criteria.add(Restrictions.and(Restrictions.le("latitude", upperBound.getLatitude()),
                Restrictions.ge("longitude", upperBound.getLongitude()),
                Restrictions.ge("latitude", lowerBound.getLatitude()),
                Restrictions.le("longitude", lowerBound.getLongitude())));

        criteria.setFirstResult(first);
        criteria.setMaxResults(size);

        List<Location> locations = criteria.list();

        return locations;
    }

    public Long countRow() {
        Criteria criteriaCount = getCurrentSession().createCriteria(Location.class);
        criteriaCount.setProjection(Projections.rowCount());
        Long count = (Long) criteriaCount.uniqueResult();
        return count;
    }

    private void initializeProxies(Location location) {
        if (location != null) {
        }
    }
}
