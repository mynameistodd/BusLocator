package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "pt")
public class Point {

    @Element(required = false)
    private int seq;

    @Element(required = false)
    private String typ;

    @Element(required = false)
    private Integer stpid;

    @Element(required = false)
    private String stpnm;

    @Element(required = false)
    private Float pdist;

    @Element(required = false)
    private double lat;

    @Element(required = false)
    private double lon;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int value) {
        this.seq = value;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String value) {
        this.typ = value;
    }

    public Integer getStpid() {
        return stpid;
    }

    public void setStpid(Integer value) {
        this.stpid = value;
    }

    public String getStpnm() {
        return stpnm;
    }

    public void setStpnm(String value) {
        this.stpnm = value;
    }

    public Float getPdist() {
        return pdist;
    }

    public void setPdist(Float value) {
        this.pdist = value;
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
