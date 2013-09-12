// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client.settings.project;

import com.google.appinventor.client.explorer.project.Project;
import com.google.appinventor.client.settings.Settings;
import com.google.appinventor.client.widgets.properties.EditableProperty;
import com.google.appinventor.shared.settings.SettingsConstants;

/**
 * Young Android project settings.
 *
 */
public final class YoungAndroidSettings extends Settings {

  /**
   * Creates a new instance of Young Android project settings.
   *
   * @param project  associated project
   */
  public YoungAndroidSettings(Project project) {
    super(SettingsConstants.PROJECT_YOUNG_ANDROID_SETTINGS);

    addProperty(new EditableProperty(this, SettingsConstants.YOUNG_ANDROID_SETTINGS_ICON,
        "", EditableProperty.TYPE_INVISIBLE));
    addProperty(new EditableProperty(this,
        SettingsConstants.YOUNG_ANDROID_SETTINGS_SHOW_HIDDEN_COMPONENTS,
        "False", EditableProperty.TYPE_INVISIBLE));
    addProperty(new EditableProperty(this,
        SettingsConstants.YOUNG_ANDROID_SETTINGS_VERSION_CODE, "1",
        EditableProperty.TYPE_INVISIBLE));
    addProperty(new EditableProperty(this,
        SettingsConstants.YOUNG_ANDROID_SETTINGS_VERSION_NAME, "1.0",
        EditableProperty.TYPE_INVISIBLE));
    addProperty(new EditableProperty(this,
        SettingsConstants.YOUNG_ANDROID_SETTINGS_USES_LOCATION, "false",
        EditableProperty.TYPE_INVISIBLE));
  }
}
