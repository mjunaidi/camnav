package com.bt.camnav.util;

public enum LocationUtil {
    INSTANCE;

    public static final double EARTH_RADIUS = 6371; // 6371km

    public double toRadians(double degree) {
        return Math.toRadians(degree);
    }
    
    public double toDegrees(double radian) {
        return Math.toDegrees(radian);
    }

    public Coordinate getUpperBound(double latitude, double longitude, double distance) {
        double bearing = 315; // 90+90+90+45 = -45 degrees
        return getCoordinate(latitude, longitude, distance, bearing);
    }

    public Coordinate getLowerBound(double latitude, double longitude, double distance) {
        double bearing = 135; // 90+45 = 135 degrees
        return getCoordinate(latitude, longitude, distance, bearing);
    }

    public Coordinate getCoordinate(double latitude, double longitude, double distance, double bearing) {
        double phi1 = toRadians(latitude);
        double lambda1 = toRadians(longitude);
        double brng = toRadians(bearing);
        double d = toRadians(distance);
        double R = toRadians(EARTH_RADIUS);
        double delta = d/R;
        
        double phi2 = Math.asin(Math.sin(phi1) * Math.cos(delta) + Math.cos(phi1) * Math.sin(delta) * Math.cos(brng));
        double lambda2 = lambda1
                + Math.atan2(Math.sin(brng) * Math.sin(delta) * Math.cos(phi1),
                        Math.cos(delta) - Math.sin(phi1) * Math.sin(phi2));

        double latitude2 = toDegrees(phi2);
        double longitude2 = toDegrees(lambda2);
        
        return new Coordinate(latitude2, longitude2);
    }
}
