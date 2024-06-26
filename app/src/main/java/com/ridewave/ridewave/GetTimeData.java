package com.ridewave.ridewave;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetTimeData {
    private Calendar mCalendar;

    public GetTimeData(Calendar calendar) {
        mCalendar = calendar;
    }

    String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getHour() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getSysDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }

    String getTextDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        return String.valueOf(sdf.format(mCalendar.getTime()));
    }
}
