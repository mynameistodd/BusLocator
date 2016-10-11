package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Affectedservice {

    @Element
    private String rt;

    @Element
    private String rtdir;

    @Element
    private Integer stpid;

    @Element
    private String stpnm;

    public String getRt() {
        return rt;
    }

    public void setRt(String value) {
        this.rt = value;
    }

    public String getRtdir() {
        return rtdir;
    }

    public void setRtdir(String value) {
        this.rtdir = value;
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

}
