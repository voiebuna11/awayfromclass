package com.example.afc.classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/*
  *
  @Author Claudiu Sarmasi
  *
  * Class created for date managing purposes
  * Beware at getMonth(). Months start at 0, not 1 on calendar. added +1
  *
  * See timezones at https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/
  *
 (*) example / pattern = dd-MMM-yyyy HH:mm:ss => 21-ian-2019 14:15:23
  *
  *
*/
public class DateUtil {
    private Date date;
    private int min;
    private int sec;

    //Calendar with time-zone
    private Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Bucharest"));

    //get date from date
    public DateUtil(Date date){
        this.date = date;
        this.cal.setTime(date);
    }

    //get date from string timestamp
    public DateUtil(String timestamp){
        this.date = new Date(Long.parseLong(timestamp) * 1000);
        this.cal.setTime(date);
    }

    public Date getDate() {
        return date;
    }

    //get string date with pattern
    public String getDate(String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public int getMonth() {
        return cal.get(Calendar.MONTH)+1;
    }

    public int getDay() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return cal.get(Calendar.MINUTE);
    }
}
