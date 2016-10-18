package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "vehicle")
public class Vehicle {

    @Element(required = false)
    private String vid;

    @Element(required = false)
    private String tmstmp;

    @Element(required = false)
    private double lat;

    @Element(required = false)
    private double lon;

    @Element(required = false)
    private int hdg;

    @Element(required = false)
    private int pid;

    @Element(required = false)
    private int pdist;

    @Element(required = false)
    private String rt;

    @Element(required = false)
    private String des;

    @Element(required = false)
    private boolean dly;

    @Element(required = false)
    private String srvtmstmp;

    @Element(required = false)
    private int spd;

    @Element(required = false)
    private Integer blk;

    @Element(required = false)
    private String tablockid;

    @Element(required = false)
    private String tatripid;

    @Element(required = false)
    private String zone;

    public String getVid() {
        return vid;
    }

    public void setVid(String value) {
        this.vid = value;
    }

    public String getTmstmp() {
        return tmstmp;
    }

    public void setTmstmp(String value) {
        this.tmstmp = value;
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

    public int getHdg() {
        return hdg;
    }

    public void setHdg(int value) {
        this.hdg = value;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int value) {
        this.pid = value;
    }

    public int getPdist() {
        return pdist;
    }

    public void setPdist(int value) {
        this.pdist = value;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String value) {
        this.rt = value;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String value) {
        this.des = value;
    }

    public boolean isDly() {
        return dly;
    }

    public void setDly(boolean value) {
        this.dly = value;
    }

    public String getSrvtmstmp() {
        return srvtmstmp;
    }

    public void setSrvtmstmp(String value) {
        this.srvtmstmp = value;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int value) {
        this.spd = value;
    }

    public Integer getBlk() {
        return blk;
    }

    public void setBlk(Integer value) {
        this.blk = value;
    }

    public String getTablockid() {
        return tablockid;
    }

    public void setTablockid(String value) {
        this.tablockid = value;
    }

    public String getTatripid() {
        return tatripid;
    }

    public void setTatripid(String value) {
        this.tatripid = value;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String value) {
        this.zone = value;
    }

}
