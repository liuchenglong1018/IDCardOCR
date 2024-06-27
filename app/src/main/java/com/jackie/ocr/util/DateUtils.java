package com.jackie.ocr.util;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static final String DB_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    // If time stamp is required in ISO 8601 format
    public static final String API_DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static String DATEFORMAT = "yyyy-MM-dd'T'HH:mm";
    private static String datePattern = "MM/dd/yyyy";
    private static String timePattern = datePattern + " HH:mm:ss";
    private static String MillisecondstoMinuteSeconds = "dd MMM yyyy hh:mm a";

    private static String uiDateTimePattern = "dd MMM yyyy hh:mm a";
    private static String ABOUT_PAGE_DATE_TIME_PATTERN = "dd MMM yyyy HH:mm:ss ";
    private static String MONTH_YEAR_PATTERN = "MMMM yyyy ";

    private static String EDITABLE_FIELD_DATE_FORMAT = "dd-MM-yyyy";

    public static final String SURVEY_DISPLAY_DATE_PATTERN = "dd MMM yyyy hh:mm a";

    /**
     * To get the current datetime
     */
    public static String getDateTimeNow() throws Exception {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    timePattern, Locale.getDefault());
            Date date = new Date();

            return dateFormat.format(date);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static Long getCurrentDate() {

        Date date = new Date();

        return date.getTime();
    }

    public static long dateToMilliSeconds(String myDate) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
            Date date = null;
            try {
                date = sdf.parse(myDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();
            return millis;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    /**
     * This method is used to take date as "dd MMM yyyy HH:mm:ss" format and return milliseconds
     *
     * @param surveyStartDateTime,surveyEndDateTime
     * @return
     */
    public static long surveyDateToMilliSeconds(String surveyStartDateTime, String surveyEndDateTime) throws Exception {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SURVEY_DISPLAY_DATE_PATTERN);
            Date d1 = null;
            Date d2 = null;
            d1 = sdf.parse(surveyStartDateTime);
            d2 = sdf.parse(surveyEndDateTime);
            long diff = d2.getTime() - d1.getTime();
            if (diff < 0) {
                diff = -(diff);
            }
            return diff;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * This method is used to take milliseconds and returns hours,minutes and seconds
     *
     * @param millis
     * @return
     */
    public static String milliSecondsToHoursMinutes(long millis) throws Exception {
        try {
            long totalSecs = millis / 1000;
            long hours = (totalSecs / 3600);
            long mins = (totalSecs / 60) % 60;
            long secs = totalSecs % 60;
            String minsString = (mins == 0)
                    ? "00"
                    : ((mins < 10)
                    ? "0" + mins
                    : "" + mins);
            String secsString = (secs == 0)
                    ? "00"
                    : ((secs < 10)
                    ? "0" + secs
                    : "" + secs);
            if (hours > 0) {

                if (hours == 1) {
                    return hours + " hr:" + minsString + " mins:" + secsString + " secs";
                } else if (hours > 1) {
                    return hours + " hrs:" + minsString + " mins:" + secsString + " secs";
                }

            } else if (mins > 0) {
                if (mins == 1) {
                    return mins + " min:" + secsString + " secs";
                } else if (mins > 1) {
                    return mins + " mins:" + secsString + " secs";
                }

            } else {
                return secsString + " secs";
            }

        } catch (Exception e) {
            throw new Exception(e);
        }
        return "";
    }


    /**
     * This method returns the current date time in the format: MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) throws Exception {
        return getDateTime(timePattern, theTime);
    }

    /**
     * This method generates a string representation of a date's date/time in the
     * format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     */
    public static final String getDateTime(String aMask, Date aDate) throws Exception {
        try {
            SimpleDateFormat df = null;
            String returnValue = "";

            if (aDate == null) {
                return "";
            } else {
                df = new SimpleDateFormat(aMask);
                returnValue = df.format(aDate);
            }

            return (returnValue);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    /* This method take a String of date format and returns today date */
    public static final String getCurrentDate(String aMask) throws Exception {
        try {
            Date date = new Date();
            String returnValue = "";
            SimpleDateFormat df = null;
            if (aMask == null) {
                return "";
            } else {
                df = new SimpleDateFormat(aMask);
                returnValue = df.format(date);
            }
            return returnValue;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static final Date yesterday() throws Exception {
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            return calendar.getTime();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /* This method take a String of date format and returns yesterday date */
    public static final String getYesterdayDate(String aMask) throws Exception {
        try {
            Date date = new Date();
            String returnValue = "";
            SimpleDateFormat df = null;
            if (aMask == null) {
                return "";
            } else {
                df = new SimpleDateFormat(aMask);
                returnValue = df.format(yesterday());
            }
            return returnValue;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * This methos id used to get the no of days
     *
     * @param nowDate
     * @param loginTime
     * @return
     */
    public static long getDifferenceDays(Long nowDate, Long loginTime) throws Exception {
        try {
            long diff = loginTime - nowDate;
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new Exception(e);
        }


    }

    /**
     * This method generates a string representation of a date/time in the format
     * you specify on input
     *
     * @param aMask   the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @throws ParseException
     * @see SimpleDateFormat
     */
    public static final Date convertStringToDate(String aMask, String strDate) throws Exception {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat(aMask);
        try {
            date = df.parse(strDate);
        } catch (Exception pe) {
            throw new Exception(pe.getMessage());
        }

        return (date);
    }

    /**
     * This method generates a string representation of a date/time in the format
     * you specify on input
     *
     * @param aMask   the date pattern the string is in
     * @param strDate a string representation of a date
     * @param tz      time zone
     * @return a converted Date object
     * @throws ParseException
     * @see SimpleDateFormat
     */
    public static final Date convertStringToDate(String aMask, String strDate, TimeZone tz) throws Exception {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat(aMask);
        df.setTimeZone(tz);

        try {
            date = df.parse(strDate);
        } catch (Exception pe) {
            throw new Exception(pe.getMessage());
        }

        return (date);
    }

    public static String dbDateFormat(String date) {
//        String date ="29/07/13";
        String dbFormat = "";
        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat output = new SimpleDateFormat(API_DATE_FORMAT_ISO8601);
        try {
            Date datesd = null;

            datesd = new SimpleDateFormat("dd-MM-yyyy").parse(date);

            dbFormat = output.format(datesd);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return dbFormat;
    }

    public static String dbDateFormatForSurveyTime(String date) throws Exception {
        try {
            SimpleDateFormat input = new SimpleDateFormat(SURVEY_DISPLAY_DATE_PATTERN);
            SimpleDateFormat output = new SimpleDateFormat(API_DATE_FORMAT_ISO8601);
            Date datesd = null;

            datesd = input.parse(date);

            String dbFormat = output.format(datesd);
            return dbFormat;
        } catch (ParseException e) {
            // e.printStackTrace();
            throw new Exception(e);
        } catch (java.text.ParseException e) {
            throw new Exception(e);
        }
    }

    public static String convertMillisToDate(long milliSeconds) throws Exception {
        try {
            if (milliSeconds > 0) {
                // Create a DateFormatter object for displaying date in specified format.
                SimpleDateFormat formatter = new SimpleDateFormat(uiDateTimePattern);

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                return formatter.format(calendar.getTime());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return "-";
    }

    public static String convertMillisToAppDate(long milliSeconds) throws Exception {
        try {
            // Create a DateFormatter object for displaying date in specified format.
            SimpleDateFormat formatter = new SimpleDateFormat(ABOUT_PAGE_DATE_TIME_PATTERN);

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static String convertEditFieldFormat(String dateString) {
        String editableDate = "";
        SimpleDateFormat input = new SimpleDateFormat(API_DATE_FORMAT_ISO8601);
        SimpleDateFormat output = new SimpleDateFormat(EDITABLE_FIELD_DATE_FORMAT);
        try {
            Date datesd = null;
            datesd = input.parse(dateString);
            editableDate = output.format(datesd);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return editableDate;

    }

    public static String aAdhaarDateFormated(String dateString) throws Exception {
        try {
            String delimeter = "/-";
            String year, month, day;
            String str1 = new String();
            String str2 = new String();
            String str3 = new String();
            StringTokenizer tokenizer = new StringTokenizer(dateString, delimeter);
            while (tokenizer.hasMoreTokens()) {
                str1 = tokenizer.nextToken();
                str2 = tokenizer.nextToken();
                str3 = tokenizer.nextToken();
            }
            if (str1.length() == 4) {
                year = str1;
                month = str2;
                day = str3;
                return String.format("%2s-%2s-%4s", day, month, year);
            } else {
                day = str1;
                month = str2;
                year = str3;
                return String.format("%2s-%2s-%4s", day, month, year);
            }
//            return "";
        } catch (Exception e) {
            throw new Exception(e);
        }
    }



    //comparing two dates
    public static final boolean compareTwoDates(String startDate, String endDate) throws Exception {
        boolean ret=true;
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
            Date d1 = sdformat.parse(startDate);
            Date d2 = sdformat.parse(endDate);

            if (d1.after(d2)) {
                ret=false;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return  ret;
    }



}
