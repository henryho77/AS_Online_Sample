package com.example.floginsample.apiTool.apiModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HenryHo on 2017/2/18.
 */

public class TaggableFriendPictureData implements Parcelable {
    public boolean is_silhouette = false;
    public String url = null;

    protected TaggableFriendPictureData(Parcel in) {
        is_silhouette = in.readByte() != 0x00;
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (is_silhouette ? 0x01 : 0x00));
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaggableFriendPictureData> CREATOR = new Parcelable.Creator<TaggableFriendPictureData>() {
        @Override
        public TaggableFriendPictureData createFromParcel(Parcel in) {
            return new TaggableFriendPictureData(in);
        }

        @Override
        public TaggableFriendPictureData[] newArray(int size) {
            return new TaggableFriendPictureData[size];
        }
    };
}
