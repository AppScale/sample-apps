// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt
package com.google.appinventor.client.editor.youngandroid.properties;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.widgets.properties.ChoicePropertyEditor;

/**
 * Property editor for button shape.
 * 
 * @author feeney.kate@gmail.com (Kate Feeney)
 */
public class YoungAndroidButtonShapeChoicePropertyEditor extends ChoicePropertyEditor {

  // Button shape choices
  private static final Choice[] shapes = new Choice[] {
    new Choice(MESSAGES.defaultButtonShape(), "0"),
    new Choice(MESSAGES.roundedButtonShape(), "1"),
    new Choice(MESSAGES.rectButtonShape(), "2"),
    new Choice(MESSAGES.ovalButtonShape(), "3")
  };

  public YoungAndroidButtonShapeChoicePropertyEditor() {
    super(shapes);
  }
}
