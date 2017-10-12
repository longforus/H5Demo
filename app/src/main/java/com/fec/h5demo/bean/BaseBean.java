package com.fec.h5demo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by XQ Yang on 2017/2/7  11:22.
 * Description : 使用ios的手段要传输的bean
 */

public class BaseBean implements Parcelable{
    public String showtype;
    public String dataString;
    public String jsid;

    public BaseBean() {
    }

    public BaseBean(Parcel in) {
        showtype = in.readString();
        dataString = in.readString();
        jsid = in.readString();
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel in) {
            return new BaseBean(in);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(showtype);
        dest.writeString(dataString);
        dest.writeString(jsid);
    }
}
