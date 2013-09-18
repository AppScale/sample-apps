// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import junit.framework.TestCase;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Tests Clock.java.
 *
 */

public class ClockTest extends TestCase {


  private final Calendar tc = Clock.MakeInstant("10/11/1941 09:30:00");

  public void testNow() throws Exception {
    assertTrue(0 > Clock.Duration(Clock.Now(), tc));
  }

  public void testMakeInstant() throws Exception {
    assertEquals("Oct 11, 1941 9:30:00 AM", Clock.FormatDateTime(tc));
  }

  public void testMakeInstantFromMillis() throws Exception {
    assertEquals("Jan 1, 1970 12:00:00 AM",
        Clock.FormatDateTime(Clock.MakeInstantFromMillis(0
                                      - TimeZone.getDefault().getRawOffset())));
  }

  public void testGetMillis() throws Exception {
    assertEquals(-TimeZone.getDefault().getRawOffset(),
        Clock.GetMillis(Clock.MakeInstant("1/1/1970 00:00:00")));
  }

  public void testAddYears() throws Exception {
    assertEquals("Oct 11, 1943 9:30:00 AM", Clock.FormatDateTime(Clock.AddYears(tc, 2)));
  }

  public void testAddYears2() throws Exception {
    assertEquals("Oct 11, 1931 9:30:00 AM", Clock.FormatDateTime(Clock.AddYears(tc, -10)));
  }

  public void testAddMonths() throws Exception {
    assertEquals("Dec 11, 1941 9:30:00 AM", Clock.FormatDateTime(Clock.AddMonths(tc, 2)));
  }

  public void testAddMonths2() throws Exception {
    assertEquals("Oct 11, 1942 9:30:00 AM", Clock.FormatDateTime(Clock.AddMonths(tc, 12)));
  }

  public void testAddWeeks() throws Exception {
    assertEquals("Sep 10, 2002 12:00:00 AM", Clock.FormatDateTime(
        Clock.AddWeeks(Clock.MakeInstant("9/11/2001 00:00:00"), 52)));
  }

  public void testAddWeeks2() throws Exception {
    assertEquals("Sep 18, 2001 12:00:00 AM", Clock.FormatDateTime(
        Clock.AddWeeks(Clock.MakeInstant("9/11/2001 00:00:00"), 1)));
  }

  public void testAddDays() throws Exception {
    assertEquals("Oct 9, 1941", Clock.FormatDate(Clock.AddDays(tc, -2)));
  }

  public void testAddDays2() throws Exception {
    assertEquals("Nov 1, 1941", Clock.FormatDate(Clock.AddDays(tc, 21)));
  }

  public void testAddHours() throws Exception {
    assertEquals("Oct 11, 1941 7:30:00 AM", Clock.FormatDateTime(Clock.AddHours(tc, -2)));
  }

  public void testAddHours2() throws Exception {
    assertEquals("Oct 12, 1941 9:30:00 AM", Clock.FormatDateTime(Clock.AddHours(tc, 24)));
  }

  public void testAddMinutes() throws Exception {
    assertEquals("Oct 11, 1941 9:32:00 AM", Clock.FormatDateTime(Clock.AddMinutes(tc, 2)));
  }

  public void testAddSeconds() throws Exception {
    assertEquals("9:31:01 AM", Clock.FormatTime(Clock.AddSeconds(tc, 61)));
  }

  public void testSecond() throws Exception {
    assertEquals(0, Clock.Second(tc));
  }

  public void testMinute() throws Exception {
    assertEquals(30, Clock.Minute(tc));
  }

  public void testHour() throws Exception {
    assertEquals(9, Clock.Hour(tc));
  }

  public void testWeekday() throws Exception {
    assertEquals(2, Clock.Weekday(Clock.MakeInstant("11/2/2009")));
  }

  public void testWeekdayName() throws Exception {
    assertEquals("Monday", Clock.WeekdayName(Clock.MakeInstant("11/2/2009")));
  }

  public void testDayOfMonth() throws Exception {
    assertEquals(11, Clock.DayOfMonth(tc));
  }

  public void testMonth() throws Exception {
    assertEquals(10, Clock.Month(tc));
  }

  public void testMonthName() throws Exception {
    assertEquals("October", Clock.MonthName(tc));
  }

  public void testYear() throws Exception {
    assertEquals(1941, Clock.Year(tc));
  }
}
