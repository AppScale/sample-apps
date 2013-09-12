// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.ViewUtil;

import android.app.Activity;
import android.view.View;

/**
 * A container for components that arranges them in tabular form.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
@DesignerComponent(version = YaVersion.TABLEARRANGEMENT_COMPONENT_VERSION,
    description = "<p>A formatting element in which to place components " +
    "that should be displayed in tabular form.</p>",
    category = ComponentCategory.ARRANGEMENTS)
@SimpleObject
public class TableArrangement extends AndroidViewComponent
    implements Component, ComponentContainer {
  private final Activity context;

  // Layout
  private final TableLayout viewLayout;

  /**
   * Creates a new TableArrangement component.
   *
   * @param container  container, component will be placed in
  */
  public TableArrangement(ComponentContainer container) {
    super(container);
    context = container.$context();

    viewLayout = new TableLayout(context, 2, 2);

    container.$add(this);
  }

  /**
   * Columns property getter method.
   *
   * @return  number of columns in this layout
   */
  @SimpleProperty(userVisible = false)
  public int Columns() {
    return viewLayout.getNumColumns();
  }

  /**
   * Columns property setter method.
   *
   * @param numColumns  number of columns in this layout
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
      defaultValue = "2")
  @SimpleProperty(userVisible = false)
  public void Columns(int numColumns) {
    viewLayout.setNumColumns(numColumns);
  }

  /**
   * Rows property getter method.
   *
   * @return  number of rows in this layout
   */
  @SimpleProperty(userVisible = false)
  public int Rows() {
    return viewLayout.getNumRows();
  }

  /**
   * Rows property setter method.
   *
   * @param numRows  number of rows in this layout
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
      defaultValue = "2")
  @SimpleProperty(userVisible = false)
  public void Rows(int numRows) {
    viewLayout.setNumRows(numRows);
  }

  // ComponentContainer implementation

  @Override
  public Activity $context() {
    return context;
  }

  @Override
  public Form $form() {
    return container.$form();
  }

  @Override
  public void $add(AndroidViewComponent component) {
    viewLayout.add(component);
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    ViewUtil.setChildWidthForTableLayout(component.getView(), width);
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    ViewUtil.setChildHeightForTableLayout(component.getView(), height);
  }

  // AndroidViewComponent implementation

  @Override
  public View getView() {
    return viewLayout.getLayoutManager();
  }
}
