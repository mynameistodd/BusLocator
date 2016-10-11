package com.sliverbit.buslocator.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "route")
public class Route implements Parcelable {

    public static final Parcelable.Creator<Route> CREATOR
            = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @Element(required = false, name = "rt")
    private String routeId;

    @Element(required = false, name = "rtnm")
    private String routeName;

    @Element(required = false, name = "rtclr")
    private String routeColor;

    @Element(required = false, name = "rtdd")
    private String routeDisplay;

    public Route() {
    }

    public Route(Parcel in) {
        routeId = in.readString();
        routeName = in.readString();
        routeColor = in.readString();
        routeDisplay = in.readString();
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String value) {
        this.routeId = value;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String value) {
        this.routeName = value;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String value) {
        this.routeColor = value;
    }

    public String getRouteDisplay() {
        return routeDisplay;
    }

    public void setRouteDisplay(String value) {
        this.routeDisplay = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routeId);
        dest.writeString(routeName);
        dest.writeString(routeColor);
        dest.writeString(routeDisplay);
    }
}
