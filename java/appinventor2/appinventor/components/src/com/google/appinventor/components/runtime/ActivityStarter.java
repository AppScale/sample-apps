// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.AnimationUtil;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

/**
 * Implementation of a general Android Activity component.
 *
 * @author markf@google.com (Mark Friedman)
 */
@DesignerComponent(version = YaVersion.ACTIVITYSTARTER_COMPONENT_VERSION,
    designerHelpDescription = "<p>A component that can launch an activity " +
    "using the <code>StartActivity</code> method.</p>" +
    "<p>Activities that can be launched include:<ul> " +
    "<li> starting other App Inventor for Android apps </li> " +
    "<li> starting the camera application </li> " +
    "<li> performing web search </li> " +
    "<li> opening a browser to a specified web page</li> " +
    "<li> opening the map application to a specified location</li></ul> " +
    "You can also launch activities that return text data.  See the " +
    "documentation on using the Activity Starter for examples." +
    "</p>",

    // TODO(user): Add more information about bringing up maps when
    // the issues with html quoting (bug 2386151) are fixed.
    description = "<p>A component that can launch an activity using " +
    "the <code>StartActivity</code> method.</p>" +
    "<p>Activities that can be launched include:<ul> " +
    "<li> Starting another App Inventor for Android app.  To do so, first " +
    "     find out the <em>class</em> of the other application by " +
    "     downloading the source code and using a file explorer or unzip " +
    "     utility to find a file named " +
    "     \"youngandroidproject/project.properties\".  The first line of " +
    "     the file will start with \"main=\" and be followed by the class " +
    "     name; for example, " +
    "     <code>main=com.gmail.Bitdiddle.Ben.HelloPurr.Screen1</code>.  " +
    "     (The first components indicate that it was created by " +
    "     Ben.Bitdiddle@gmail.com.)  To make your " +
    "     <code>ActivityStarter</code> launch this application, set the " +
    "     following properties: <ul> " +
    "     <li> <code>ActivityPackage</code> to the class name, dropping the " +
    "          last component (for example, " +
    "          <code>com.gmail.Bitdiddle.Ben.HelloPurr</code>)</li> " +
    "     <li> <code>ActivityClass</code> to the entire class name (for " +
    "          example, " +
    "          <code>com.gmail.Bitdiddle.Ben.HelloPurr.Screen1</code>)</li> " +
    "     </ul></li>" +
    "<li> Starting the camera application by setting the following " +
    "     properties:<ul> " +
    "     <li> <code>Action: android.intent.action.MAIN</code> </li> " +
    "     <li> <code>ActivityPackage: com.android.camera</code> </li> " +
    "     <li> <code>ActivityClass: com.android.camera.Camera</code></li> " +
    "     </ul></li>" +
    "<li> Performing web search.  Assuming the term you want to search " +
    "     for is \"vampire\" (feel free to substitute your own choice), " +
    "     set the properties to:<blockquote><code> " +
    "     Action: android.intent.action.WEB_SEARCH<br/> " +
    "     ExtraKey: query<br/> " +
    "     ExtraValue: vampire<br/> " +
    "     ActivityPackage: com.google.android.providers.enhancedgooglesearch<br/>" +
    "     ActivityClass: com.google.android.providers.enhancedgooglesearch.Launcher<br/> " +
    "     </code></blockquote></li> " +
    "<li> Opening a browser to a specified web page.  Assuming the page you " +
    "     want to go to is \"www.facebook.com\" (feel free to substitute " +
    "     your own choice), set the properties to: <blockquote><code> " +
    "     Action: android.intent.action.VIEW <br/> " +
    "     DataUri: http://www.facebook.com </code> </blockquote> </li> " +
    "</ul>" +
    "</p>",
    category = ComponentCategory.MISC,
    nonVisible = true,
    iconName = "images/activityStarter.png")
@SimpleObject
public class ActivityStarter extends AndroidNonvisibleComponent
    implements ActivityResultListener, Component, Deleteable {

  private String action;
  private String dataUri;
  private String dataType;
  private String activityPackage;
  private String activityClass;
  private String extraKey;
  private String extraValue;
  private String resultName;
  private Intent resultIntent;
  private String result;
  private int requestCode;
  private final ComponentContainer container;

  /**
   * Creates a new ActivityStarter component.
   *
   * @param container  container, kept for access to form and context
   */
  public ActivityStarter(ComponentContainer container) {
    super(container.$form());
    // Save the container for later
    this.container = container;
    result = "";
    Action(Intent.ACTION_MAIN);
    ActivityPackage("");
    ActivityClass("");
    DataUri("");
    DataType("");
    ExtraKey("");
    ExtraValue("");
    ResultName("");
  }

  /**
   * Returns the action that will be used to start the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String Action() {
    return action;
  }

  /**
   * Specifies the action that will be used to start the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void Action(String action) {
    this.action = action.trim();
  }

  // TODO(lizlooney) - currently we support just one extra name/value pair that will be passed to
  // the activity. The user specifies the ExtraKey and ExtraValue properties.
  // We should allow more extra name/value pairs, but we'd need a different interface with regard
  // to properties and functions.
  // In the documentation for Intent, they use the term "name", not "key", and we might want to use
  // the term "name", also.
  // There are backwards compatibility issues with removing the ExtraKey and ExtraValue properties.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.putExtra method.
  // Adding multiple functions like PutStringExtra, PutStringArrayExtra, PutCharExtra,
  // PutCharArrayExtra, PutBooleanExtra, PutBooleanArrayExtra, PutByteExtra, PutByteArrayExtra,
  // PutShortExtra, PutShortArrayExtra, PutIntExtra, PutIntArrayExtra, PutLongExtra,
  // PutLongArrayExtra, PutFloatExtra, PutFloatArrayExtra, PutDoubleExtra, PutDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the extra key that will be passed to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey() {
    return extraKey;
  }

  /**
   * Specifies the extra key that will be passed to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey(String extraKey) {
    this.extraKey = extraKey.trim();
  }


  /**
   * Returns the extra value that will be passed to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue() {
    return extraValue;
  }

  /**
   * Specifies the extra value that will be passed to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue(String extraValue) {
    this.extraValue = extraValue.trim();
  }

  // TODO(lizlooney) - currently we support retrieving just one string extra result from the
  // activity. The user specifies the ResultName property and, then after the activity finishes,
  // the string extra result corresponding to ResultName is passed as the result parameter to the
  // AfterActivity event and is also available from the Result property getter.
  // We should allow access to more extra results, but we'd need a different interface with regard
  // to properties, functions, and events parameters.
  // There are backwards compatibility issues with removing the AfterActivity event's result
  // parameter and the Result property.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.get...Extra method.
  // Adding multiple functions like GetStringExtra, GetStringArrayExtra, GetCharExtra,
  // GetCharArrayExtra, GetBooleanExtra, GetBooleanArrayExtra, GetByteExtra, GetByteArrayExtra,
  // GetShortExtra, GetShortArrayExtra, GetIntExtra, GetIntArrayExtra, GetLongExtra,
  // GetLongArrayExtra, GetFloatExtra, GetFloatArrayExtra, GetDoubleExtra, GetDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the name that will be used to retrieve a result from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultName() {
    return resultName;
  }

  /**
   * Specifies the name that will be used to retrieve a result from the
   * activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ResultName(String resultName) {
    this.resultName = resultName.trim();
  }

  /**
   * Returns the result from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String Result() {
    return result;
  }

  /**
   * Returns the data URI that will be used to start the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String DataUri() {
    return dataUri;
  }

  /**
   * Specifies the data URI that will be used to start the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void DataUri(String dataUri) {
    this.dataUri = dataUri.trim();
  }

  /**
   * Returns the MIME type to pass to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String DataType() {
    return dataType;
  }

  /**
   * Specifies the MIME type to pass to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void DataType(String dataType) {
    this.dataType = dataType.trim();
  }

  /**
   * Returns the package part of the specific component that will be started.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ActivityPackage() {
    return activityPackage;
  }

  /**
   * Specifies the package part of the specific component that will be started.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ActivityPackage(String activityPackage) {
    this.activityPackage = activityPackage.trim();
  }

  /**
   * Returns the class part of the specific component that will be started.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ActivityClass() {
    return activityClass;
  }

  /**
   * Specifies the class part of the specific component that will be started.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ActivityClass(String activityClass) {
    this.activityClass = activityClass.trim();
  }

  @SimpleEvent(description = "Event raised after this ActivityStarter returns.")
  public void AfterActivity(String result) {
    EventDispatcher.dispatchEvent(this, "AfterActivity", result);
  }

  /**
   * Returns the MIME type from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultType() {
    if (resultIntent != null) {
      String resultType = resultIntent.getType();
      if (resultType != null) {
        return resultType;
      }
    }
    return "";
  }

  /**
   * Returns the URI from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultUri() {
    if (resultIntent != null) {
      String resultUri = resultIntent.getDataString();
      if (resultUri != null) {
        return resultUri;
      }
    }
    return "";
  }


  /**
   * Returns the name of the activity that corresponds to this ActivityStarer,
   * or an empty string if no corresponding activity can be found.
   */
  @SimpleFunction(description = "Returns the name of the activity that corresponds to this " +
      "ActivityStarer, or an empty string if no corresponding activity can be found.")
  public String ResolveActivity() {
    Intent intent = buildActivityIntent();
    PackageManager pm = container.$context().getPackageManager();
    ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
    if (resolveInfo != null && resolveInfo.activityInfo != null) {
      return resolveInfo.activityInfo.name;
    }
    return "";
  }

  /**
   * Start the activity.
   */
  @SimpleFunction(description = "Start the activity corresponding to this ActivityStarter.")
  public void StartActivity() {
    resultIntent = null;
    result = "";

    Intent intent = buildActivityIntent();

    if (requestCode == 0) {
      // First time, we need to register this as an ActivityResultListener with the Form.
      // The Form's onActivityResult method will be called when the activity returns. If we
      // register with the Form and then use the requestCode when we start an activity, the Form
      // will call our resultReturned method.
      requestCode = form.registerForActivityResult(this);
    }

    try {
      container.$context().startActivityForResult(intent, requestCode);
      String openAnim = container.$form().getOpenAnimType();
      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);
    } catch (ActivityNotFoundException e) {
      form.dispatchErrorOccurredEvent(this, "StartActivity",
          ErrorMessages.ERROR_ACTIVITY_STARTER_NO_CORRESPONDING_ACTIVITY);
    }
  }

  private Intent buildActivityIntent() {
    Uri uri = (dataUri.length() != 0) ? Uri.parse(dataUri) : null;
    Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);

    if (dataType.length() != 0) {
      if (uri != null) {
        intent.setDataAndType(uri, dataType);
      } else {
        intent.setType(dataType);
      }
    }

    if (activityPackage.length() != 0 || activityClass.length() != 0) {
      ComponentName component = new ComponentName(activityPackage, activityClass);
      intent.setComponent(component);
    }

    if (extraKey.length() != 0 && extraValue.length() != 0) {
      intent.putExtra(extraKey, extraValue);
    }

    return intent;
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == this.requestCode) {
      Log.i("ActivityStarter", "resultReturned - resultCode = " + resultCode);
      if (resultCode == Activity.RESULT_OK) {
        resultIntent = data;
        if (resultName.length() != 0 && resultIntent != null &&
            resultIntent.hasExtra(resultName)) {
          result = resultIntent.getStringExtra(resultName);
        } else {
          result = "";
        }
        // call user's AfterActivity event handler
        AfterActivity(result);
      }
    }
  }

  @SimpleEvent(description = "The ActivityError event is no longer used. " +
      "Please use the Screen.ErrorOccurred event instead.",
      userVisible = false)
  public void ActivityError(String message) {
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    form.unregisterForActivityResult(this);
  }
}
