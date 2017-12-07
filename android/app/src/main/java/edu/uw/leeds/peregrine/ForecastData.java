package edu.uw.leeds.peregrine;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

/**
 * Created by John Akers on 10/10/2017.
 *
 * Basic data object used for storing information about
 * individual forecasts.
 *
 * Default constructor used with public fields because
 * these objects only exist within the application and other
 * examples did the same thing.
 */
public class ForecastData implements Parcelable {
    public Date time;
    public String temperature;
    public String weather;
    public String icon;
    public double latitude;
    public double longitude;

    //default constructor
    public ForecastData() {}

    //constructor for parcelable
    protected ForecastData(Parcel in) {
        long tmpTime = in.readLong();
        time = tmpTime != -1 ? new Date(tmpTime) : null;
        temperature = in.readString();
        weather = in.readString();
        icon = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time != null ? time.getTime() : -1L);
        dest.writeString(temperature);
        dest.writeString(weather);
        dest.writeString(icon);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @SuppressWarnings("unused") // TODO @Jakersnorth remove or modify
    public static final Parcelable.Creator<ForecastData> CREATOR = new Parcelable.Creator<ForecastData>() {
        @Override
        public ForecastData createFromParcel(Parcel in) {
            return new ForecastData(in);
        }

        @Override
        public ForecastData[] newArray(int size) {
            return new ForecastData[size];
        }
    };
}