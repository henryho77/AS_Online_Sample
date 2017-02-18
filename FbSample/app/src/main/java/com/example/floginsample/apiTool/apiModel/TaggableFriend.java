package com.example.floginsample.apiTool.apiModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HenryHo on 2017/2/18.
 */

public class TaggableFriend implements Parcelable {
    public String id = null;
    public String name = null;
    public ArrayList<TaggableFriendPicture> picture = null;

    protected TaggableFriend(Parcel in) {
        id = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            picture = new ArrayList<TaggableFriendPicture>();
            in.readList(picture, TaggableFriendPicture.class.getClassLoader());
        } else {
            picture = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        if (picture == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(picture);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaggableFriend> CREATOR = new Parcelable.Creator<TaggableFriend>() {
        @Override
        public TaggableFriend createFromParcel(Parcel in) {
            return new TaggableFriend(in);
        }

        @Override
        public TaggableFriend[] newArray(int size) {
            return new TaggableFriend[size];
        }
    };
}