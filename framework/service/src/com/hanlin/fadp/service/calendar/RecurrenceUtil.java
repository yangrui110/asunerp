
package com.hanlin.fadp.service.calendar;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.icu.util.Calendar;

/**
 * Recurrence Utilities
 */
public class RecurrenceUtil {

    /** Returns a Date object from a String. */
    public static Date parseDate(String dateStr) {
        String formatString = "";

        if (dateStr.length() == 16)
            dateStr = dateStr.substring(0, 14);
        if (dateStr.length() == 15)
            formatString = "yyyyMMdd'T'hhmmss";
        if (dateStr.length() == 8)
            formatString = "yyyyMMdd";

        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        ParsePosition pos = new ParsePosition(0);

        return formatter.parse(dateStr, pos);
    }

    /** Returns a List of parsed date strings. */
    public static List<Date> parseDateList(List<String> dateList) {
        List<Date> newList = new ArrayList<Date>();

        if (dateList == null)
            return newList;
        for (String value: dateList)
            newList.add(parseDate(value));
        return newList;
    }

    /** Returns a String from a Date object */
    public static String formatDate(Date date) {
        String formatString = "";
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        if (cal.isSet(Calendar.MINUTE))
            formatString = "yyyyMMdd'T'hhmmss";
        else
            formatString = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);

        return formatter.format(date);
    }

    /** Returns a Llist of date strings from a List of Date objects */
    public static List<String> formatDateList(List<Date> dateList) {
        List<String> newList = new ArrayList<String>();
        for (Date date: dateList)
            newList.add(formatDate(date));
        return newList;
    }

    /** Returns the time as of now. */
    public static long now() {
        return (new Date()).getTime();
    }

}

