package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Error {

    @Element
    private String pid;

    @Element
    private String rt;

    @Element
    private String msg;

    @Element
    private Integer stpid;

    @Element
    private String vid;

    @Element
    private String rtdir;

    @Element
    private String dir;

    public String getPid() {
        return pid;
    }

    public void setPid(String value) {
        this.pid = value;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String value) {
        this.rt = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String value) {
        this.msg = value;
    }

    public Integer getStpid() {
        return stpid;
    }

    public void setStpid(Integer value) {
        this.stpid = value;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String value) {
        this.vid = value;
    }

    public String getRtdir() {
        return rtdir;
    }

    public void setRtdir(String value) {
        this.rtdir = value;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String value) {
        this.dir = value;
    }

}
