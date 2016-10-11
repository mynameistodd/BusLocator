package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Pt {

    @Element
    private int seq;

    @Element
    private String typ;

    @Element
    private Integer stpid;

    @Element
    private String stpnm;

    @Element
    private Float pdist;

    @Element
    private double lat;

    @Element
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
