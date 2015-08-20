package com.sliverbit.buslocator.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * BusLocator
 * Created by tdeland on 8/19/15.
 */
public class RouteName implements Parcelable {
    public static final Parcelable.Creator<RouteName> CREATOR
            = new Parcelable.Creator<RouteName>() {
        public RouteName createFromParcel(Parcel in) {
            return new RouteName(in);
        }

        public RouteName[] newArray(int size) {
            return new RouteName[size];
        }
    };
    private String name;
    private String routeAbbr;
    private String routeOffsetID;

    private RouteName(Parcel in) {
        name = in.readString();
        routeAbbr = in.readString();
        routeOffsetID = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteAbbr() {
        return routeAbbr;
    }

    public void setRouteAbbr(String routeAbbr) {
        this.routeAbbr = routeAbbr;
    }

    public String getRouteOffsetID() {
        return routeOffsetID;
    }

    public void setRouteOffsetID(String routeOffsetID) {
        this.routeOffsetID = routeOffsetID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(routeAbbr);
        dest.writeString(routeOffsetID);
    }
}
