// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client.editor.youngandroid.palette;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.appinventor.client.editor.simple.SimpleComponentDatabase;
import com.google.appinventor.client.editor.simple.components.MockComponent;
import com.google.appinventor.client.editor.simple.palette.DropTargetProvider;
import com.google.appinventor.client.editor.simple.palette.SimpleComponentDescriptor;
import com.google.appinventor.client.editor.simple.palette.SimplePaletteItem;
import com.google.appinventor.client.editor.simple.palette.SimplePalettePanel;
import com.google.appinventor.client.editor.youngandroid.YaFormEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidAlignmentChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidAssetSelectorPropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidBooleanPropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidButtonShapeChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidColorChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidComponentSelectorPropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidFontTypefaceChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidHorizontalAlignmentChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidLegoNxtSensorPortChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidScreenAnimationChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidScreenOrientationChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidSensorDistIntervalChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidSensorTimeIntervalChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidVerticalAlignmentChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidVisibilityChoicePropertyEditor;
import com.google.appinventor.client.editor.youngandroid.properties.YoungAndroidTextReceivingPropertyEditor;
import com.google.appinventor.client.widgets.properties.FloatPropertyEditor;
import com.google.appinventor.client.widgets.properties.IntegerPropertyEditor;
import com.google.appinventor.client.widgets.properties.NonNegativeFloatPropertyEditor;
import com.google.appinventor.client.widgets.properties.NonNegativeIntegerPropertyEditor;
import com.google.appinventor.client.widgets.properties.PropertyEditor;
import com.google.appinventor.client.widgets.properties.StringPropertyEditor;
import com.google.appinventor.client.widgets.properties.TextPropertyEditor;
import com.google.appinventor.common.version.AppInventorFeatures;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.shared.simple.ComponentDatabaseInterface.PropertyDefinition;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel showing Simple components which can be dropped onto the Young Android
 * visual designer panel.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public class YoungAndroidPalettePanel extends Composite implements SimplePalettePanel {

  // Component database: information about components (including their properties and events)
  private static final SimpleComponentDatabase COMPONENT_DATABASE =
      SimpleComponentDatabase.getInstance();

  // Associated editor
  private final YaFormEditor editor;

  private final StackPanel stackPalette;
  private final Map<ComponentCategory, VerticalPanel> categoryPanels;

  /**
   * Creates a new component palette panel.
   *
   * @param editor    parent editor of this panel
   */
  public YoungAndroidPalettePanel(YaFormEditor editor) {
    this.editor = editor;

    stackPalette = new StackPanel();

    categoryPanels = new HashMap<ComponentCategory, VerticalPanel>();

    for (ComponentCategory category : ComponentCategory.values()) {
      if (showCategory(category)) {
        VerticalPanel categoryPanel = new VerticalPanel();
        categoryPanel.setWidth("100%");
        categoryPanels.put(category, categoryPanel);
        stackPalette.add(categoryPanel, category.getName());
      }
    }

    stackPalette.setWidth("100%");
    initWidget(stackPalette);
  }

  private static boolean showCategory(ComponentCategory category) {
    if (category == ComponentCategory.UNINITIALIZED) {
      return false;
    }
    if (category == ComponentCategory.INTERNAL &&
        !AppInventorFeatures.showInternalComponentsCategory()) {
      return false;
    }
    return true;
  }

  /**
   * Loads all components to be shown on this palette.  Specifically, for
   * each component (except for those whose category is UNINITIALIZED, or
   * whose category is INTERNAL and we're running on a production server,
   * or who are specifically marked as not to be shown on the palette),
   * this creates a corresponding {@link SimplePaletteItem} with the passed
   * {@link DropTargetProvider} and adds it to the panel corresponding to
   * its category.
   *
   * @param dropTargetProvider  provider of targets that palette items can be
   *                            dropped on
   */
  @Override
  public void loadComponents(DropTargetProvider dropTargetProvider) {
    for (String component : COMPONENT_DATABASE.getComponentNames()) {
      String categoryString = COMPONENT_DATABASE.getCategoryString(component);
      String helpString = COMPONENT_DATABASE.getHelpString(component);
      String categoryDocUrlString = COMPONENT_DATABASE.getCategoryDocUrlString(component);
      Boolean showOnPalette = COMPONENT_DATABASE.getShowOnPalette(component);
      Boolean nonVisible = COMPONENT_DATABASE.getNonVisible(component);
      ComponentCategory category = ComponentCategory.valueOf(categoryString);
      if (showOnPalette && showCategory(category)) {
        addPaletteItem(new SimplePaletteItem(
            new SimpleComponentDescriptor(component, editor, helpString,
                categoryDocUrlString, showOnPalette, nonVisible),
            dropTargetProvider),
                       category);
      }
    }
  }

  @Override
  public void configureComponent(MockComponent mockComponent) {
    String componentType = mockComponent.getType();

    // Configure properties
    for (PropertyDefinition property : COMPONENT_DATABASE.getPropertyDefinitions(componentType)) {
      mockComponent.addProperty(property.getName(), property.getDefaultValue(),
          property.getCaption(), createPropertyEditor(property.getEditorType()));
    }
  }

  /*
   * Creates a new property editor.
   */
  private PropertyEditor createPropertyEditor(String editorType) {
    if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_HORIZONTAL_ALIGNMENT)) {
      return new YoungAndroidHorizontalAlignmentChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_VERTICAL_ALIGNMENT)) {
        return new YoungAndroidVerticalAlignmentChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_ASSET)) {
      return new YoungAndroidAssetSelectorPropertyEditor(editor);
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_BLUETOOTHCLIENT)) {
      return new YoungAndroidComponentSelectorPropertyEditor(editor,
          // Pass the set of component types that will be shown in the property editor,
          // in this case, just "BluetoothClient".
          Collections.singleton("BluetoothClient"));
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN)) {
      return new YoungAndroidBooleanPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_BUTTON_SHAPE)) {
      return new YoungAndroidButtonShapeChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_COLOR)) {
      return new YoungAndroidColorChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_COMPONENT)) {
      return new YoungAndroidComponentSelectorPropertyEditor(editor);
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_FLOAT)) {
      return new FloatPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_INTEGER)) {
      return new IntegerPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_LEGO_NXT_SENSOR_PORT)) {
      return new YoungAndroidLegoNxtSensorPortChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_LEGO_NXT_GENERATED_COLOR)) {
      return new YoungAndroidColorChoicePropertyEditor(
          YoungAndroidColorChoicePropertyEditor.NXT_GENERATED_COLORS);
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT)) {
      return new NonNegativeFloatPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER)) {
      return new NonNegativeIntegerPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_SCREEN_ORIENTATION)) {
      return new YoungAndroidScreenOrientationChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_SCREEN_ANIMATION)) {
      return new YoungAndroidScreenAnimationChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_SENSOR_DIST_INTERVAL)) {
      return new YoungAndroidSensorDistIntervalChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_SENSOR_TIME_INTERVAL)) {
      return new YoungAndroidSensorTimeIntervalChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_STRING)) {
      return new StringPropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_TEXTALIGNMENT)) {
      return new YoungAndroidAlignmentChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_TYPEFACE)) {
      return new YoungAndroidFontTypefaceChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_VISIBILITY)) {
      return new YoungAndroidVisibilityChoicePropertyEditor();
    } else if (editorType.equals(PropertyTypeConstants.PROPERTY_TYPE_TEXT_RECEIVING)) {
      return new YoungAndroidTextReceivingPropertyEditor();
    } else {
      return new TextPropertyEditor();
    }
  }

  /*
   * Adds a component entry to the palette.
   */
  private void addPaletteItem(SimplePaletteItem component, ComponentCategory category) {
    VerticalPanel panel = categoryPanels.get(category);
    panel.add(component);
  }
}
