// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime.util;

import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Implementation of various date and time related runtime functions.
 *
 */
@SimpleObject
public final class Dates {

  /**
   * Date/time interval kinds.
   */
  public static final int DATE_YEAR = Calendar.YEAR;
  public static final int DATE_MONTH = Calendar.MONTH;
  public static final int DATE_DAY = Calendar.DATE;
  public static final int DATE_WEEK = Calendar.WEEK_OF_YEAR;
  public static final int DATE_HOUR = Calendar.HOUR_OF_DAY;
  public static final int DATE_MINUTE = Calendar.MINUTE;
  public static final int DATE_SECOND = Calendar.SECOND;

  /**
   * Days of the week.
   */
  public static final int DATE_MONDAY = Calendar.MONDAY;
  public static final int DATE_TUESDAY = Calendar.TUESDAY;
  public static final int DATE_WEDNESDAY = Calendar.WEDNESDAY;
  public static final int DATE_THURSDAY = Calendar.THURSDAY;
  public static final int DATE_FRIDAY = Calendar.FRIDAY;
  public static final int DATE_SATURDAY = Calendar.SATURDAY;
  public static final int DATE_SUNDAY = Calendar.SUNDAY;

  /**
   * Months.
   */
  public static final int DATE_JANUARY = Calendar.JANUARY;
  public static final int DATE_FEBRUARY = Calendar.FEBRUARY;
  public static final int DATE_MARCH = Calendar.MARCH;
  public static final int DATE_APRIL = Calendar.APRIL;
  public static final int DATE_MAY = Calendar.MAY;
  public static final int DATE_JUNE = Calendar.JUNE;
  public static final int DATE_JULY = Calendar.JULY;
  public static final int DATE_AUGUST = Calendar.AUGUST;
  public static final int DATE_SEPTEMBER = Calendar.SEPTEMBER;
  public static final int DATE_OCTOBER = Calendar.OCTOBER;
  public static final int DATE_NOVEMBER = Calendar.NOVEMBER;
  public static final int DATE_DECEMBER = Calendar.DECEMBER;

  private Dates() {  // COV_NF_LINE
  }                  // COV_NF_LINE

  /**
   * Adds a time interval to the given date.
   *
   * @param date  date to add to
   * @param intervalKind  kind of interval
   * @param interval  units to add
   */
  @SimpleFunction
  public static void DateAdd(Calendar date, int intervalKind, int interval) {
    switch (intervalKind) {
      default:
        throw new IllegalArgumentException("illegal date/time interval kind in function DateAdd()");

      case DATE_YEAR:
      case DATE_MONTH:
      case DATE_DAY:
      case DATE_WEEK:
      case DATE_HOUR:
      case DATE_MINUTE:
      case DATE_SECOND:
        date.add(intervalKind, interval);
        break;
    }
  }

  /**
   * Creates a date from the given string.
   *
   * <p>Dates must be formatted as follows:<br>
   *   <b>MM/DD/YYYY hh:mm:ss</b><br>
   * or<br>
   *   <b>MM/DD/YYYY</b><br>
   * or<br>
   *   <b>hh:mm</b><br>
   * where MM is the month (01-12), DD the day (01-31), YYYY the year
   * (0000-9999), hh the hours (00-23), mm the minutes (00-59) and ss
   * the seconds (00-59).
   *
   * @param value  string to convert
   * @return  date
   */
  @SimpleFunction
  public static Calendar DateValue(String value) {
    Calendar date = new GregorianCalendar();
    try {
      DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      dateTimeFormat.setLenient(true);
      date.setTime(dateTimeFormat.parse(value));
    } catch (ParseException e) {
      try {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(true);
        date.setTime(dateFormat.parse(value));
      } catch (ParseException e1) {
        try {
          DateFormat dateFormat = new SimpleDateFormat("HH:mm");
          dateFormat.setLenient(true);
          date.setTime(dateFormat.parse(value));
        } catch (ParseException pe) {
          throw new IllegalArgumentException("illegal date/time format in function DateValue()");
        }
      }
    }
    return date;
  }

  /**
   * Returns the day of the month for the given date.
   *
   * @param date  date to get day of
   * @return  day (range 1 - 31)
   */
  @SimpleFunction
  public static int Day(Calendar date) {
    return date.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Converts and formats the given date into a date and time string.
   *
   * @see SimpleDateFormat
   *
   * @param date  date to format
   * @return  formatted date
   */
  @SimpleFunction
  public static String FormatDateTime(Calendar date) {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
        .format(date.getTime());
  }

  /**
   * Converts and formats the given date into a date string.
   *
   * @see SimpleDateFormat
   *
   * @param date  date to format
   * @return  formatted date
   */
  @SimpleFunction
  public static String FormatDate(Calendar date) {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.getTime());
  }

  /**
   * Converts and formats the given date into a time string.
   *
   * @see SimpleDateFormat
   *
   * @param date  date to format
   * @return  formatted date
   */
  @SimpleFunction
  public static String FormatTime(Calendar date) {
    return DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date.getTime());
  }

  /**
   * Returns the hours for the given date.
   *
   * @param date  date to use hours of
   * @return  hours (range 0 - 23)
   */
  @SimpleFunction
  public static int Hour(Calendar date) {
    return date.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Returns the minutes for the given date.
   *
   * @param date  date to use minutes of
   * @return  minutes (range 0 - 59)
   */
  @SimpleFunction
  public static int Minute(Calendar date) {
    return date.get(Calendar.MINUTE);
  }

  /**
   * Returns the month of the given date.
   *
   * @param date  date to use month of
   * @return  month: {@link #DATE_JANUARY}, {@link #DATE_FEBRUARY},
   *                 {@link #DATE_MARCH}, {@link #DATE_APRIL},
   *                 {@link #DATE_MAY}, {@link #DATE_JUNE},
   *                 {@link #DATE_JULY}, {@link #DATE_AUGUST},
   *                 {@link #DATE_SEPTEMBER}, {@link #DATE_OCTOBER},
   *                 {@link #DATE_NOVEMBER}, {@link #DATE_DECEMBER}
   */
  @SimpleFunction
  public static int Month(Calendar date) {
    return date.get(Calendar.MONTH);
  }

  /**
   * Returns the name of the month for the given date.
   *
   * @param date  date to use month of
   * @return  name of month
   */
  @SimpleFunction
  public static String MonthName(Calendar date) {
    return String.format("%1$tB", date);
  }

  /**
   * Returns the current date and time.
   *
   * @return  current date and time
   */
  @SimpleFunction
  public static Calendar Now() {
    return new GregorianCalendar();
  }

  /**
   * Returns the seconds for the given date.
   *
   * @param date  date to use seconds of
   * @return  seconds (range 0 - 59)
   */
  @SimpleFunction
  public static int Second(Calendar date) {
    return date.get(Calendar.SECOND);
  }

  /**
   * Returns the current system time in milliseconds.
   *
   * @return  current system time in milliseconds
   */
  @SimpleFunction
  public static long Timer() {
    return System.currentTimeMillis();
  }

  /**
   * Returns the weekday for the given date.
   *
   * @param date  date to use weekday of
   * @return  weekday: {@link #DATE_SUNDAY}, {@link #DATE_MONDAY},
   *                   {@link #DATE_TUESDAY}, {@link #DATE_WEDNESDAY},
   *                   {@link #DATE_THURSDAY}, {@link #DATE_FRIDAY},
   *                   {@link #DATE_SATURDAY}
   */
  @SimpleFunction
  public static int Weekday(Calendar date) {
    return date.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Returns the name of the weekday for the given date.
   *
   * @param date  date to use weekday of
   * @return  name of weekday
   */
  @SimpleFunction
  public static String WeekdayName(Calendar date) {
    return String.format("%1$tA", date);
  }

  /**
   * Returns the year of the given date.
   *
   * @param date  date to use year of
   * @return  year
   */
  @SimpleFunction
  public static int Year(Calendar date) {
    return date.get(Calendar.YEAR);
  }
}
