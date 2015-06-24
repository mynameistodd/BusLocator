package com.sliverbit.buslocator.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tdeland on 5/18/15.
 */
public class Location {
    private int adherence;
    private String altTimepoint;
    private String busNum;
    private String crossingTime;
    private String Id;
    private String lat;
    private String longitude;
    private String routeAbbr;
    private String routeDirection;
    private String timePointAltName;
    private String timePointName;
    private String timePointStopID;
    private String timestamp;

    public String getAdherence() {
        if (adherence == 0) {
            return "On time";
        } else if (adherence > 0) {
            return adherence + " min ahead";
        } else if (adherence < 0) {
            return adherence + " min behind";
        } else {
            return "unknown";
        }
    }

    public String getAltTimepoint() {
        return altTimepoint;
    }

    public String getBusNum() {
        return busNum;
    }

    public String getCrossingTime() {
        return crossingTime;
    }


    public String getRouteDirection() {
        return routeDirection;
    }


    public String getId() {
        return Id;
    }


    public String getLat() {
        return lat.substring(0, 2) + "." + lat.substring(2); //TODO: proper parsing
    }


    public String getLongitude() {
        return longitude.substring(0, 3) + "." + longitude.substring(3); //TODO: proper parsing
    }

    public String getTimePointAltName() {
        return timePointAltName;
    }


    public String getTimestamp() {
        String returnVal = timestamp;
        SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm:ss.SSSSSSS");
        SimpleDateFormat sdfDest = new SimpleDateFormat("m");
        try {
            Date date = sdfSource.parse(timestamp);
            returnVal = sdfDest.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnVal + " min ago";
    }

    public String getRouteAbbr() {
        return routeAbbr;
    }


    public String getTimePointName() {
        return timePointName;
    }


    public String getTimePointStopID() {
        return timePointStopID;
    }
}
