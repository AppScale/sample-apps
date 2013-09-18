// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.apphosting.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for parsing the various components of the AppId.
 *
 */
public class AppId {
  private static final int APP_ID_MAX_LEN = 100;
  private static final Pattern DISPLAY_APP_ID_RE =
      Pattern.compile("[a-z\\d\\-]{1," + APP_ID_MAX_LEN + "}", Pattern.CASE_INSENSITIVE);
  private static final Pattern DOMAIN_RE =
      Pattern.compile("([a-z\\d\\-\\.]{1," + APP_ID_MAX_LEN + "})?\\:", Pattern.CASE_INSENSITIVE);
  private static final Pattern PARTITION_RE =
      Pattern.compile("([a-z\\d\\-]{1," + APP_ID_MAX_LEN + "})?\\~", Pattern.CASE_INSENSITIVE);
  private static final Pattern APP_ID_RE =
      Pattern.compile(
          "(?:" + PARTITION_RE + ")?((?:" + DOMAIN_RE + ")?(" + DISPLAY_APP_ID_RE + "))",
          Pattern.CASE_INSENSITIVE);

  private String appId;
  private String domain;
  private String longAppId;
  private String displayAppId;
  private String partition;

  /**
   * Create a new AppId based on an appId formatted as follows:
   *
   * [(partition)~][(domain):](display-app-id)
   *
   * @param appId The appId to parse.
   */
  private AppId(String appId) {
    this.appId = appId;
    if (appId == null || appId.length() == 0) {
      return;
    }
    Matcher matcher = APP_ID_RE.matcher(appId);
    if (!matcher.matches()) {
      return;
    }
    this.partition = matcher.group(1);
    this.longAppId = matcher.group(2);
    this.domain = matcher.group(3);
    this.displayAppId = matcher.group(4);
  }

  /**
   * @return The full appId.
   */
  public String getAppId() {
    return appId;
  }

  /**
   * @return The domain component of this appId.
   */
  public String getDomain() {
    return domain;
  }

  /**
   * @return The display-app-id component of this appId.
   */
  public String getDisplayAppId() {
    return displayAppId;
  }

  /**
   * @return The partition component of the appId.
   */
  public String getPartition() {
    return partition;
  }

  /**
   * @return The appId without the partition component.
   */
  public String getLongAppId() {
    return longAppId;
  }

  /**
   * Returns a new AppId object based on an appId formatted as follows:
   *
   * [(partition)~][(domain):](display-app-id)
   *
   * @param appId The appId to parse.
   *
   * @return AppId object with the parsed appid components.
   */
  public static AppId parse(String appId) {
    return new AppId(appId);
  }
}
