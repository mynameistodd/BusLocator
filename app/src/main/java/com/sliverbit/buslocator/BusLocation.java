package com.sliverbit.buslocator;

/**
 * Created by tdeland on 5/18/15.
 */
public class BusLocation {
    private String adherence;
    private String altTimepoint;
    private String bus;
    private String crossing;
    private String direction;
    private String id;
    private String lattitude;
    private String longitude;
    private String timepoint;
    private String timestamp;

    public String getAdherence() {
        return adherence;
    }

    public void setAdherence(String adherence) {
        this.adherence = adherence;
    }

    public String getAltTimepoint() {
        return altTimepoint;
    }

    public void setAltTimepoint(String altTimepoint) {
        this.altTimepoint = altTimepoint;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getCrossing() {
        return crossing;
    }

    public void setCrossing(String crossing) {
        this.crossing = crossing;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLattitude() {
        return lattitude.substring(0,2) + "." + lattitude.substring(2); //TODO: proper parsing
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude.substring(0,3) + "." + longitude.substring(3); //TODO: proper parsing
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(String timepoint) {
        this.timepoint = timepoint;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
