package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Vehicle {

    @Element
    private String vid;

    @Element
    private String tmpstmp;

    @Element
    private double lat;

    @Element
    private double lon;

    @Element
    private int hdg;

    @Element
    private int pid;

    @Element
    private int pdist;

    @Element
    private String rt;

    @Element
    private String des;

    @Element
    private boolean dly;

    @Element
    private String srvtmstmp;

    @Element
    private int spd;

    @Element
    private Integer blk;

    @Element
    private String tablockid;

    @Element
    private String tatripid;

    @Element
    private String zone;

    public String getVid() {
        return vid;
    }

    public void setVid(String value) {
        this.vid = value;
    }

    public String getTmpstmp() {
        return tmpstmp;
    }

    public void setTmpstmp(String value) {
        this.tmpstmp = value;
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
