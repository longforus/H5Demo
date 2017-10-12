package com.fec.h5demo.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by XQ Yang on 2017/2/8  16:53.
 * Description :
 */

public class JsonBean implements Parcelable {

    /**
     * type : mcvc
     * class : TestViewController
     * showtype : push
     * dataString : js string
     */

    private String type;
    @SerializedName("class")
    private String classX;
    private String showtype;
    private String dataString;

    protected JsonBean(Parcel in) {
        type = in.readString();
        classX = in.readString();
        showtype = in.readString();
        dataString = in.readString();
    }

    public static final Creator<JsonBean> CREATOR = new Creator<JsonBean>() {
        @Override
        public JsonBean createFromParcel(Parcel in) {
            return new JsonBean(in);
        }

        @Override
        public JsonBean[] newArray(int size) {
            return new JsonBean[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassX() {
        return classX;
    }

    public void setClassX(String classX) {
        this.classX = classX;
    }

    public String getShowtype() {
        return showtype;
    }

    public void setShowtype(String showtype) {
        this.showtype = showtype;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(classX);
        dest.writeString(showtype);
        dest.writeString(dataString);
    }
}
