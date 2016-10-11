package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Prediction {

    @Element
    private String tmstmp;

    @Element
    private String typ;

    @Element
    private int stpid;

    @Element
    private String stpnm;

    @Element
    private int vid;

    @Element
    private int dstp;

    @Element
    private String rt;

    @Element
    private String rtdd;

    @Element
    private String rtdir;

    @Element
    private String des;

    @Element
    private String prdtm;

    @Element
    private Boolean dly0020;

    @Element
    private String tablockid;

    @Element
    private String tatripid;

    @Element
    private String zone;

    public String getTmstmp() {
        return tmstmp;
    }

    public void setTmstmp(String value) {
        this.tmstmp = value;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String value) {
        this.typ = value;
    }

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

    public int getVid() {
        return vid;
    }

    public void setVid(int value) {
        this.vid = value;
    }

    public int getDstp() {
        return dstp;
    }

    public void setDstp(int value) {
        this.dstp = value;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String value) {
        this.rt = value;
    }

    public String getRtdd() {
        return rtdd;
    }

    public void setRtdd(String value) {
        this.rtdd = value;
    }

    public String getRtdir() {
        return rtdir;
    }

    public void setRtdir(String value) {
        this.rtdir = value;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String value) {
        this.des = value;
    }

    public String getPrdtm() {
        return prdtm;
    }

    public void setPrdtm(String value) {
        this.prdtm = value;
    }

    public Boolean isDly_0020() {
        return dly0020;
    }

    public void setDly_0020(Boolean value) {
        this.dly0020 = value;
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
