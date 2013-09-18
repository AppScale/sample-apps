// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.server.storage;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import java.util.Date;

import javax.persistence.Id;

/**
 * Classes for the data objects that are stored in the Objectify database.
 *
 * TODO(user): for now I just defined a bunch of classes (parallel
 * to the ones in the old ode.proto). It might be
 * worth considering whether to make these classes extend DAOBasic (does it buy
 * us anything) and whether to add any methods for manipulating the objects.
 *
 * TODO(user): consider separating these out into individual class
 * files - more Java-y?
 *
 * @author sharon@google.com (Sharon Perl)
 *
 */
public class StoredData {
  // The UserData class is an entity root, and the parent of UserFileData
  // and UserProjectData
  @Unindexed
  public static final class UserData {
    // The Google Account userid
    @Id public String id;

    @Indexed public String email;

    // User settings
    public String settings;

    // Has user accepted terms of service?
    boolean tosAccepted;

    @Indexed public Date visited; // Used to figure out if a user is active. Timestamp when settings are stored.

  }

  // Project properties
  // The ProjectData class is an entity root, and the parent of FileData
  @Unindexed
  static final class ProjectData {
    // Auto-generated unique project id
    @Id Long id;

    // Verbose project name
    String name;

    // Project type. Currently Simple and YoungAndroid
    // TODO(user): convert to enum
    String type;

    // Global project settings
    String settings;

    // Date project created
    // TODO(): Make required
    long dateCreated;

    // Date project last modified
    // TODO(): Make required
    long dateModified;

    // The specially formatted project history
    String history;
  }

  // Project properties specific to the user
  @Unindexed
  static final class UserProjectData {
    enum StateEnum {
      CLOSED,
      OPEN,
      DELETED
    }

    // The project id
    @Id long projectId;

    // The user (parent's) key
    @Parent Key<UserData> userKey;

    // State of the project relative to user
    // TODO(user): is this ever used?
    StateEnum state;

    // User specific project settings
    // TODO(user): is this ever used?
    String settings;
  }

  // Non-project-specific files (tied to user)
  @Unindexed
  static final class UserFileData {
    // The file name
    @Id String fileName;

    // The user (parent's) key
    @Parent Key<UserData> userKey;

    // File content, these are raw bytes. Note that Objectify automatically
    // converts byte[] to Blob.
    byte[] content;

    // File settings
    // TODO(user): is this ever used?
    String settings;
  }

  // Project files
  @Unindexed
  static final class FileData {
    // The role that file play: source code, build target or temporary file
    enum RoleEnum {
      SOURCE,
      TARGET,
      TEMPORARY
    }

    // The file name
    @Id String fileName;

    // Key of the project (parent) to which this file belongs
    @Parent Key<ProjectData> projectKey;

    // File role
    RoleEnum role;

    // File content, these are raw bytes. Note that Objectify automatically
    // converts byte[] to an App Engine Datastore Blob (which is not the same thing as a Blobstore
    // Blob).  Consequently, if isBlob is true, the content field should be ignored and the data
    // should be retrieved from Blobstore.
    byte[] content;

    // Is this file stored in Blobstore.  If it is, the blobstorePath will contain the path to use
    // to retrieve the data from Blobstore.
    boolean isBlob;

    // The Blobstore path to use to get the data from Blobstore
    String blobstorePath;

    // File settings
    String settings;
  }

  // MOTD data.
  @Unindexed
  static final class MotdData {
    // Unique Id - for now we expect there to be only 1 MotdData object.
    @Id Long id;

    // Caption for the MOTD
    String caption;

    // More MOTD detail, if any
    String content;
  }

  // Rendezvous Data -- Only used when memcache is unavailable
  @Unindexed
  static final class RendezvousData {
    @Id Long id;

    // Six character key entered by user (or scanned).
    @Indexed public String key;

    // Ip Address of phone
    public String ipAddress;

    public Date used;           // Used during (manual) cleanup to determine if this entry can be pruned

  }

  @Unindexed
  static final class WhiteListData {
    @Id Long id;
    @Indexed public String emailLower;
  }
}
