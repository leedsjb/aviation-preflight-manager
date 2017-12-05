package edu.uw.leeds.peregrine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Airport Object for storing airport information
 *  modified to be parcelable
 */

public class AirportData implements Parcelable {
    public String code;
    public String name;
    public boolean delayed;
    public String weather;
    public String delayReason;
    public String delayType;

    public AirportData(){}

    protected AirportData(Parcel in) {
        code = in.readString();
        name = in.readString();
        delayed = in.readByte() != 0x00;
        weather = in.readString();
        delayReason = in.readString();
        delayType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeByte((byte) (delayed ? 0x01 : 0x00));
        dest.writeString(weather);
        dest.writeString(delayReason);
        dest.writeString(delayType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AirportData> CREATOR = new Parcelable.Creator<AirportData>() {
        @Override
        public AirportData createFromParcel(Parcel in) {
            return new AirportData(in);
        }

        @Override
        public AirportData[] newArray(int size) {
            return new AirportData[size];
        }
    };
}
