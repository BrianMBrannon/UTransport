package com.example.utransport;

import android.text.format.Time;

/**
 * Created by bubba on 2/11/14.
 */
public class MobileDevice {

    //Android API
    Time myTime;
    {
        myTime = new Time();
    }

    private int hour = myTime.hour;
    private int minute = myTime.minute;

    public int getHour() {
        return hour;
    }

    public int minute() {
        return minute;
    }

    public int getAllMinutes() {
        return minute + hour * 60;
    }
}
