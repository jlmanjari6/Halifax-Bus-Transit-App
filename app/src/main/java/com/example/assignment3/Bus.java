package com.example.assignment3;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

class Bus implements Parcelable {
    private String vehicleID;
    private double latitude;
    private double longitude;
    private List<Bus> busMarkers = new ArrayList<>();

    public Bus(List<Bus> busMarkers) {
        this.busMarkers = busMarkers;
    }

    public Bus(String vehicleID, double latitude, double longitude) {
        this.vehicleID = vehicleID;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Bus(Parcel in) {
        busMarkers = in.readArrayList(Bus.class.getClassLoader());
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Bus> getBusMarkers() {
        return busMarkers;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(busMarkers);
    }

    public final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}