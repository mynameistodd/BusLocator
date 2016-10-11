package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root
public class Ptr {

    @Element
    private int pid;

    @Element
    private int ln;

    @Element
    private String rtdir;

    @Element
    private List<Pt> pt;

    public int getPid() {
        return pid;
    }

    public void setPid(int value) {
        this.pid = value;
    }

    public int getLn() {
        return ln;
    }

    public void setLn(int value) {
        this.ln = value;
    }

    public String getRtdir() {
        return rtdir;
    }

    public void setRtdir(String value) {
        this.rtdir = value;
    }

    public List<Pt> getPt() {
        if (pt == null) {
            pt = new ArrayList<Pt>();
        }
        return this.pt;
    }

}
