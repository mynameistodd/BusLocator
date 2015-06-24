package com.sliverbit.buslocator.models;

/**
 * Created by tdeland on 6/22/15.
 */
public class StopsOnRoute {
    private String abbreviation;
    private String description;
    private String directionID;
    private String directionName;
    private String isTimePoint;
    private String lattitude;
    private String longitude;
    private String name;
    private String sequence;
    private String stopID;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirectionID() {
        return directionID;
    }

    public void setDirectionID(String directionID) {
        this.directionID = directionID;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getIsTimePoint() {
        return isTimePoint;
    }

    public void setIsTimePoint(String isTimePoint) {
        this.isTimePoint = isTimePoint;
    }

    public String getLattitude() {
        return lattitude.substring(0, 2) + "." + lattitude.substring(2); //TODO: proper parsing
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude.substring(0, 3) + "." + longitude.substring(3); //TODO: proper parsing
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }
}
