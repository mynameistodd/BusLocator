package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "bustime-response")
public class BustimeResponse {

    @ElementList(required = false)
    private List<Error> error;

    @ElementList(required = false)
    private List<String> dir;

    @ElementList(name = "ptr", required = false, inline = true)
    private List<Pattern> pattern;

    @ElementList(required = false)
    private List<Prediction> prd;

    @ElementList(name = "route", required = false, inline = true)
    private List<Route> route;

    @ElementList(required = false)
    private List<Servicebulletin> sb;

    @ElementList(required = false)
    private List<Stop> stop;

    @Element(required = false)
    private String tm;

    @ElementList(name = "vehicle", required = false, inline = true)
    private List<Vehicle> vehicle;

    public BustimeResponse() {
    }

    public List<Error> getError() {
        if (error == null) {
            error = new ArrayList<Error>();
        }
        return this.error;
    }

    public List<String> getDir() {
        if (dir == null) {
            dir = new ArrayList<String>();
        }
        return this.dir;
    }

    public List<Pattern> getPattern() {
        if (pattern == null) {
            pattern = new ArrayList<Pattern>();
        }
        return this.pattern;
    }

    public List<Prediction> getPrd() {
        if (prd == null) {
            prd = new ArrayList<Prediction>();
        }
        return this.prd;
    }

    public List<Route> getRoute() {
        if (route == null) {
            route = new ArrayList<Route>();
        }
        return this.route;
    }

    public List<Servicebulletin> getSb() {
        if (sb == null) {
            sb = new ArrayList<Servicebulletin>();
        }
        return this.sb;
    }

    public List<Stop> getStop() {
        if (stop == null) {
            stop = new ArrayList<Stop>();
        }
        return this.stop;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String value) {
        this.tm = value;
    }

    public List<Vehicle> getVehicle() {
        if (vehicle == null) {
            vehicle = new ArrayList<Vehicle>();
        }
        return this.vehicle;
    }

}
