// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime.util;

import android.view.Gravity;
import com.google.appinventor.components.common.ComponentConstants;
import com.google.appinventor.components.runtime.LinearLayout;

/**
 * Utilities for centering contents of arrangements and screen
 *
 * @author hal@mit.edu (Hal Abelson)
 */
public class AlignmentUtil {
  LinearLayout viewLayout;

  public AlignmentUtil(LinearLayout viewLayout) {
    this.viewLayout = viewLayout;
  }

  /**
   * Set the horizontal alignment (gravity) of the alignment
   * Throws an IllegalArgumentException if alignment has illegal value.
   * @param alignment
   */
  public void setHorizontalAlignment (int alignment) throws IllegalArgumentException {
    switch (alignment) {
      case ComponentConstants.GRAVITY_LEFT:
        viewLayout.setHorizontalGravity(Gravity.LEFT);
        break;
      case ComponentConstants.GRAVITY_RIGHT:
        viewLayout.setHorizontalGravity(Gravity.RIGHT);
        break;
      case ComponentConstants.GRAVITY_CENTER_HORIZONTAL:
        viewLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        break;
      default:
        throw new IllegalArgumentException("Bad value to setHorizontalAlignment: " + alignment);
    }
  }

  /**
   * Set the vertical alignment (gravity) of the alignment
   * Throws an IllegalArgumentException if alignment has illegal value.
   * @param alignment
   */
  public void setVerticalAlignment (int alignment) throws IllegalArgumentException {
    switch (alignment) {
      case ComponentConstants.GRAVITY_TOP:
        viewLayout.setVerticalGravity(Gravity.TOP);
        break;
      case ComponentConstants.GRAVITY_CENTER_VERTICAL:
        viewLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        break;
      case ComponentConstants.GRAVITY_BOTTOM:
        viewLayout.setVerticalGravity(Gravity.BOTTOM);
        break;
      default:
        throw new IllegalArgumentException("Bad value to setVerticalAlignment: " + alignment);
    }
  }
}






