package com.example.mapsample.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HenryHo on 2017/2/22.
 */

public class StationPropertyResult implements Parcelable {
    public String id = null;
    public String timeTableNo = null;
    public String stationName = null;
    public String brance = null;
    public String address = null;
    public String tel = null;
    public float latitude = 0.0f;
    public float longitude = 0.0f;

    protected StationPropertyResult(Parcel in) {
        id = in.readString();
        timeTableNo = in.readString();
        stationName = in.readString();
        brance = in.readString();
        address = in.readString();
        tel = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(timeTableNo);
        dest.writeString(stationName);
        dest.writeString(brance);
        dest.writeString(address);
        dest.writeString(tel);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StationPropertyResult> CREATOR = new Parcelable.Creator<StationPropertyResult>() {
        @Override
        public StationPropertyResult createFromParcel(Parcel in) {
            return new StationPropertyResult(in);
        }

        @Override
        public StationPropertyResult[] newArray(int size) {
            return new StationPropertyResult[size];
        }
    };
}
