package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Stop {

    @Element
    private int stpid;

    @Element
    private String stpnm;

    @Element
    private double lat;

    @Element
    private double lon;

    public int getStpid() {
        return stpid;
    }

    public void setStpid(int value) {
        this.stpid = value;
    }

    public String getStpnm() {
        return stpnm;
    }

    public void setStpnm(String value) {
        this.stpnm = value;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double value) {
        this.lat = value;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double value) {
        this.lon = value;
    }

}
