// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client.settings;

import com.google.appinventor.client.widgets.properties.EditableProperties;
import com.google.appinventor.client.widgets.properties.PropertyChangeListener;

/**
 * Superclass for all specific settings.
 *
 */
public abstract class Settings extends EditableProperties implements PropertyChangeListener {
  // Category for settings (used for encoding the settings)
  private final String category;

  /**
   * Creates a new set of settings.
   *
   * @param category category for settings
   */
  protected Settings(String category) {
    super(false);

    this.category = category;

    addPropertyChangeListener(this);
  }

  /**
   * Called before encoding to allow the settings to update any values if
   * necessary.
   */
  protected void updateBeforeEncoding() {
  }

  /**
   * Called after decoding to allow the settings to update any values if
   * necessary.
   */
  protected void updateAfterDecoding() {
  }

  @Override
  protected String encode(boolean all) {
    updateBeforeEncoding();
    return super.encode(all);
  }

  @Override
  protected String getPrefix() {
    return '"' + category + "\":{";
  }

  @Override
  protected String getSuffix() {
    return "}";
  }

  // PropertyChangeListener implementation

  /**
   * {@inheritDoc}
   *
   * Subclasses may override this change listener in order to react to property
   * changes immediately.
   */
  @Override
  public void onPropertyChange(String propertyName, String newValue) {
  }
}
