package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "ptr")
public class Pattern {

    @Element(required = false, name = "pid")
    private int patternId;

    @Element(required = false, name = "ln")
    private Float patternLength;

    @Element(required = false, name = "rtdir")
    private String routeDirection;

    @ElementList(required = false, name = "pt", inline = true)
    private List<Point> point;

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int value) {
        this.patternId = value;
    }

    public Float getPatternLength() {
        return patternLength;
    }

    public void setPatternLength(Float value) {
        this.patternLength = value;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String value) {
        this.routeDirection = value;
    }

    public List<Point> getPoint() {
        if (point == null) {
            point = new ArrayList<Point>();
        }
        return this.point;
    }

}
