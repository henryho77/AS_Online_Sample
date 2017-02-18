package com.example.floginsample.apiTool.apiModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HenryHo on 2017/2/18.
 */

public class TaggableFriendsResult2 implements Parcelable {
    public ArrayList<TaggableFriend> data = null;

    protected TaggableFriendsResult2(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<TaggableFriend>();
            in.readList(data, TaggableFriend.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<TaggableFriendsResult2> CREATOR = new Creator<TaggableFriendsResult2>() {
        @Override
        public TaggableFriendsResult2 createFromParcel(Parcel in) {
            return new TaggableFriendsResult2(in);
        }

        @Override
        public TaggableFriendsResult2[] newArray(int size) {
            return new TaggableFriendsResult2[size];
        }
    };
}
