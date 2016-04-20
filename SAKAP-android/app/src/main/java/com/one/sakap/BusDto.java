package com.one.sakap;

/**
 * Created by cscmehmet on 12.08.2015.
 */
public class BusDto {

    private String name;
    private String x;
    private String y;
    private Double distance;
    private String nextLocation;
    private String prevLocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(String nextLocation) {
        this.nextLocation = nextLocation;
    }

    public String getPrevLocation() {
        return prevLocation;
    }

    public void setPrevLocation(String prevLocation) {
        this.prevLocation = prevLocation;
    }

}
