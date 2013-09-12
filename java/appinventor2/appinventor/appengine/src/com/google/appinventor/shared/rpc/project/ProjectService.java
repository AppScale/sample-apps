// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.shared.rpc.project;

import com.google.appinventor.shared.rpc.RpcResult;
import com.google.appinventor.shared.rpc.ServerLayout;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * Interface for the service providing project information.
 *
 */
@RemoteServiceRelativePath(ServerLayout.PROJECT_SERVICE)
public interface ProjectService extends RemoteService {

  /**
   * Creates a new project.
   * @param projectType type of new project
   * @param projectName name of new project
   * @param params optional parameters (project type dependent)
   *
   * @return a {@link UserProject} for new project
   */
  UserProject newProject(String projectType, String projectName,
                         NewProjectParameters params);

  /**
   * Copies a project with a new name.
   * @param oldProjectId  old project ID
   * @param newName  new name of project
   *
   * @return a {@link UserProject} for new project
   */
  UserProject copyProject(long oldProjectId, String newName);

  /**
   * Deletes a project.
   * @param projectId  project ID
   */
  void deleteProject(long projectId);

  /**
   * Returns an array with project IDs.
   *
   * @return  IDs of projects found by the back-end
   */
  long[] getProjects();

  /**
   * Returns a list of project infos.
   * @return list of project infos found by the back-end
   */
  List<UserProject> getProjectInfos();

  /**
   * Returns the root node for the given project.
   * @param projectId  project ID as received by
   *                   {@link #getProjects()}
   *
   * @return  root node of project
   */
  ProjectRootNode getProject(long projectId);

  /**
   * Returns a string with the project settings.
   * @param projectId  project ID
   *
   * @return  settings
   */
  String loadProjectSettings(long projectId);

  /**
   * Stores a string with the project settings.
   * @param projectId  project ID
   * @param settings  project settings
   */
  void storeProjectSettings(long projectId, String settings);

  /**
   * Deletes a file in the given project.
   * @param projectId  project ID
   * @param fileId  ID of file to delete
   * @return modification date for project
   */
  long deleteFile(long projectId, String fileId);

  /**
   * Deletes all files that are contained directly in the given directory. Files
   * in subdirectories are not deleted.
   * @param projectId project ID
   * @param directory path of the directory
   * @return modification date for project
   */
  long deleteFiles(long projectId, String directory);

  /**
   * Loads the file information associated with a node in the project tree. The
   * actual return value depends on the file kind. Source (text) files should
   * typically return their contents. Image files will be more likely to return
   * the URL that the browser can find them at.
   *
   * @param projectId  project ID
   * @param fileId  project node whose source should be loaded
   *
   * @return  implementation dependent
   */
  String load(long projectId, String fileId);

  /**
   * Loads the file information associated with a node in the project tree. The
   * actual return value is the raw file contents.
   *
   * @param projectId  project ID
   * @param fileId  project node whose source should be loaded
   *
   * @return  raw file content
   */
  byte[] loadraw(long projectId, String fileId);

  /**
   * Loads the contents of multiple files.
   *
   * @param files  list containing file descriptor of files to be loaded
   * @return  list containing file descriptors and their associated content
   */
  List<FileDescriptorWithContent> load(List<FileDescriptor> files);

  /**
   * Saves the content of the file associated with a node in the project tree.
   *
   * @param projectId  project ID
   * @param fileId  project node whose source should be saved
   * @param content  content to be saved
   * @return modification date for project
   *
   * @see #load(long, String)
   */
  long save(long projectId, String fileId, String content);

  /**
   * Saves the contents of multiple files.
   *
   * @param filesAndContent  list containing file descriptor and their
   *                         associated content
   * @return modification date for last modified project of list
   */
  public long save(List<FileDescriptorWithContent> filesAndContent);

  /**
   * Invokes a build command for the project on the back-end.
   *
   * @param projectId  project ID
   * @param target  build target (optional, implementation dependent)
   *
   * @return  results of invoking the build command
   */
  RpcResult build(long projectId, String target);

  /**
   * Gets the result of a build command for the project from the back-end.
   *
   * @param projectId  project ID
   * @param target  build target (optional, implementation dependent)
   *
   * @return  results of build. The following values may be in RpcResult.result:
   *            0: Build is done and was successful
   *            1: Build is done and was unsuccessful
   *           -1: Build is not yet done.
   */
  RpcResult getBuildResult(long projectId, String target);

  /**
   * Adds a new file to the given project.
   *
   * @param projectId  project id
   * @param fileId  id of file to add
   * @return modification date for project
   */
  long addFile(long projectId, String fileId);
}
