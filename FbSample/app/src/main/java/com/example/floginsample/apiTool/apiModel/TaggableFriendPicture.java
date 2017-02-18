package com.example.floginsample.apiTool.apiModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HenryHo on 2017/2/18.
 */

public class TaggableFriendPicture implements Parcelable {
    public ArrayList<TaggableFriendPictureData> data = null;

    protected TaggableFriendPicture(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<TaggableFriendPictureData>();
            in.readList(data, TaggableFriendPictureData.class.getClassLoader());
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
    public static final Parcelable.Creator<TaggableFriendPicture> CREATOR = new Parcelable.Creator<TaggableFriendPicture>() {
        @Override
        public TaggableFriendPicture createFromParcel(Parcel in) {
            return new TaggableFriendPicture(in);
        }

        @Override
        public TaggableFriendPicture[] newArray(int size) {
            return new TaggableFriendPicture[size];
        }
    };
}