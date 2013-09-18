// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.server.flags;

/**
 * A flag associated with a system property.
 *
 * Values for flags are specified in the &lt;system-properties&gt; section of appengine-web.xml.
 *
 * @param <T> The flag value type.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public abstract class Flag<T> {
  private final String name;
  private final T defaultValue;

  protected Flag(String name, T defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  /**
   * Get the current value of this flag.
   *
   * @return the current value of this flag.
   */
  public final T get() throws IllegalFlagValueException {
    String value = System.getProperty(name);
    return (value == null) ? defaultValue : convert(value);
  }

  /**
   * Sets the value of this flag for testing purposes.
   *
   * @param value the value to assign to this flag
   */
  public final void setForTest(T value) {
    System.setProperty(name, value.toString());
  }

  /**
   * Converts the given value into the correct type for this flag.
   *
   * @return the value converted into the correct type
   * @throws IllegalFlagValueException if the value cannot be converted into the correct type
   */
  protected abstract T convert(String value) throws IllegalFlagValueException;

  /**
   * Creates a String flag.
   *
   * @param name the system property name associated with the flag
   * @param defaultValue the value to use if the system property is not set
   */
  public static Flag<String> createFlag(String name, String defaultValue) {
    return new Flag<String>(name, defaultValue) {
      @Override
      protected String convert(String value) throws IllegalFlagValueException {
        return value;
      }
    };
  }

  /**
   * Creates a Boolean flag.
   *
   * @param name the system property name associated with the flag
   * @param defaultValue the value to use if the system property is not set
   */
  public static Flag<Boolean> createFlag(String name, boolean defaultValue) {
    return new Flag<Boolean>(name, defaultValue) {
      @Override
      protected Boolean convert(String value) throws IllegalFlagValueException {
        // Return true iff value is equal, ignoring case, to the string "true".
        return Boolean.valueOf(value);
      }
    };
  }

  /**
   * Creates an Integer flag.
   *
   * @param name the system property name associated with the flag
   * @param defaultValue the value to use if the system property is not set
   */
  public static Flag<Integer> createFlag(final String name, int defaultValue) {
    return new Flag<Integer>(name, defaultValue) {
      @Override
      protected Integer convert(String value) throws IllegalFlagValueException {
        try {
          return Integer.parseInt(value);
        } catch (NumberFormatException e) {
          throw new IllegalFlagValueException("The value for the integer flag named \"" + name +
              "\" is not an integer: " + value);
        }
      }
    };
  }

  /**
   * Creates a Float flag.
   *
   * @param name the system property name associated with the flag
   * @param defaultValue the value to use if the system property is not set
   */
  public static Flag<Float> createFlag(final String name, float defaultValue) {
    return new Flag<Float>(name, defaultValue) {
      @Override
      protected Float convert(String value) throws IllegalFlagValueException {
        try {
          return Float.parseFloat(value);
        } catch (NumberFormatException e) {
          throw new IllegalFlagValueException("The value for the float flag named \"" + name +
              "\" is not a float: " + value);
        }
      }
    };
  }
}
