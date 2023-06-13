package com.wingcopter.UAVSimulator.model;

public class GnssData {
    private double startLatitude; // Starting latitude in degrees
    private double startLongitude; // Starting longitude in degrees
    private double endLatitude; // Ending latitude in degrees
    private double endLongitude; // Ending longitude in degrees
    private double speed; // Movement speed in meters per second
    private double altitude; // Altitude in degrees
    double changeAltMeterPerSecond; // Change of Altitude in meter per second

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getChangeAltMeterPerSecond() {
        return changeAltMeterPerSecond;
    }

    public void setChangeAltMeterPerSecond(double changeAltMeterPerSecond) {
        this.changeAltMeterPerSecond = changeAltMeterPerSecond;
    }
}
