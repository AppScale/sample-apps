// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt
package com.google.appinventor.components.runtime.util;

import java.util.Locale;

/**
 * This abstracts out what a text to speech implementation needs to have.  In particular we use
 * this to enable having an implementation that uses the internal Android TextToSpeech library for
 * post-Donut devices and uses an externally installed library for the earlier devices.
 *
 * @author markf@google.com (Mark Friedman)
 */
public interface ITextToSpeech {

  /**
   * Callback specifying methods for successful and failed attempts to generate speech.
   */
  interface TextToSpeechCallback {

    /**
     * Callback to be invoked when a message has finished being spoken.
     */
    public void onSuccess();

    /**
     * Callback to be invoked when we have a failure to communicate.
     */
    public void onFailure();
  }

  /**
   * Speaks the given message corresponding to the language and country of the given locale.
   * @param message the message to speak
   * @param loc the locale to use
   */
  public void speak(String message, Locale loc);

  /**
   * This will be called when the Activity is stopped, to give us a chance to cleanup resources,
   * if necessary.
   */
  public void onStop();

  /**
   * This will be called when the Activity is resumed, to give us a chance to re-initialize
   * resources, if necessary.
   */
  public void onResume();

}
