package com.example.utransport;

import android.text.format.Time;

/**
 * Created by bubba on 2/11/14.
 */
public class MobileDevice {

    //Android API
    private int hour;
    private int minute;
    private String dayOfWeek;

    public MobileDevice() {
        Time myTime;
        {
            myTime = new Time();
        }
        myTime.setToNow();
        this.hour = myTime.hour;
        this.minute = myTime.minute;
        switch(myTime.weekDay) {
            case 0:
                dayOfWeek = "SUNDAY";
                break;
            case 6:
                dayOfWeek = "SATURDAY";
                break;
            default:
                dayOfWeek = "WEEKDAY";
                break;
        }
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getAllMinutes() {
        return minute + hour * 60;
    }

    public String getWeekday() {
        return dayOfWeek;
    }
}
