// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client.editor;

import com.google.appinventor.client.Ode;
import com.google.appinventor.shared.rpc.project.FileNode;
import com.google.appinventor.shared.rpc.project.ProjectRootNode;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

/**
 * Abstract superclass for all file editors.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public abstract class FileEditor extends Composite {

  // The project editor that contains this file editor.
  protected final ProjectEditor projectEditor;

  // FileNode associated with this file editor
  protected final FileNode fileNode;

  /**
   * Creates a {@code FileEditor} instance.
   *
   * @param projectEditor  the project editor that contains this file editor
   * @param fileNode  FileNode associated with this file editor
   */
  public FileEditor(ProjectEditor projectEditor, FileNode fileNode) {
    this.projectEditor = projectEditor;
    this.fileNode = fileNode;
  }

  /**
   * Returns the project editor associated with this file editor.
   *
   * @return  project editor associated with this file editor
   */
  public final ProjectEditor getProjectEditor() {
    return projectEditor;
  }

  /**
   * Returns the project ID associated with this file editor.
   *
   * @return  project ID associated with this file editor
   */
  public final long getProjectId() {
    return fileNode.getProjectId();
  }

  /**
   * Returns the project root node associated with this file editor.
   *
   * @return  project root node associated with this file editor
   */
  public final ProjectRootNode getProjectRootNode() {
    return fileNode.getProjectRoot();
  }

  /**
   * Returns the file ID associated with this file editor.
   *
   * @return  file ID associated with this file editor
   */
  public final String getFileId() {
    return fileNode.getFileId();
  }

  /**
   * Returns the file node associated with this file editor.
   *
   * @return  file node associated with this file editor
   */
  public final FileNode getFileNode() {
    return fileNode;
  }

  /**
   * Loads the content of the file into the editor.
   *
   * @param afterFileLoaded  optional command to be executed after the file has
   *                         been loaded
   */
  public abstract void loadFile(Command afterFileLoaded);

  /**
   * Returns the text that should appear on the tab for this file editor.
   */
  public abstract String getTabText();

  /**
   * Called when the FileEditor is about to be shown. Calls
   * Ode.getInstance().setCurrentFileEditor(this). The subclass should do anything else necessary
   * to get its UI set up after calling super.onShow().
   */
  public void onShow() {
    Ode.getInstance().setCurrentFileEditor(this);
  }

  /**
   * Called when the FileEditor is about to be hidden. Calls
   * Ode.getInstance().setCurrentFileEditor(null). The subclass should do anything else necessary
   * to get its UI cleaned up after calling super.onHide().
   */
  public void onHide() {
    // When an editor is detached, if we are the "current" editor,
    // set the current editor to null.
    // Note: I'm not sure it is possible that we would not be the "current"
    // editor when this is called, but we check just to be safe.
    if (Ode.getInstance().getCurrentFileEditor() == this) {
      Ode.getInstance().setCurrentFileEditor(null);
    }
  }

  /**
   * Called when the FileEditor is about to be closed. Subclasses can override this
   * to remove themselves as listeners.
   */
  public void onClose() {
  }

  /**
   * Called to trigger the Repl (Wireless) to start. This version does nothing
   * but the YaBlocksEditor overrides this version with one that start the
   * Repl going.
   */
  public void startRepl(Boolean alreadyRunning, Boolean forEmulator) {
  }

  /**
   * Gets the raw content of the file associated with the editor.
   * This method is used when the EditorManager saves modified files.
   */
  public abstract String getRawFileContent();

  /**
   * Invoked after a save operation completes successfully.
   */
  public abstract void onSave();
}
