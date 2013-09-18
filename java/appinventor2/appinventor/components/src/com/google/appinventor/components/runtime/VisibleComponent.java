// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;

/**
 * Superclass of visible components in the runtime libraries.
 * <p>
 * Defines standard properties and events.
 *
 */
@SimpleObject
public abstract class VisibleComponent implements Component {
  protected VisibleComponent() {
  }

  /**
   * Width property getter method.
   *
   * @return  width property used by the layout
   */
  @SimpleProperty(
      category = PropertyCategory.APPEARANCE)
  public abstract int Width();

  /**
   * Width property setter method.
   *
   * @param width  width property used by the layout
   */
  @SimpleProperty
  public abstract void Width(int width);

  /**
   * Height property getter method.
   *
   * @return  height property used by the layout
   */
  @SimpleProperty(
      category = PropertyCategory.APPEARANCE)
  public abstract int Height();

  /**
   * Height property setter method.
   *
   * @param height  height property used by the layout
   */
  @SimpleProperty
  public abstract void Height(int height);
}
