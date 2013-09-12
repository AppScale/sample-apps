package com.google.apphosting.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parsed Version Id.
 *
 */
public class VersionId {
  public static final String DEFAULT_ENGINE_ID = "default";
  public static final String BUILTINS_PREFIX = "ah-builtin-";

  private static final int ENGINE_ID_MAX_LENGTH = 63;
  private static final int ENGINE_VERSION_ID_MAX_LENGTH = 100;
  private static final int MINOR_VERSION_ID_LENGTH = 20;

  private static final String ENGINE_ID_RE =
      String.format("[a-z\\d][a-z\\d\\-]{0,%d}", ENGINE_ID_MAX_LENGTH - 1);

  private static final String ENGINE_VERSION_ID_RE =
      String.format("[a-z\\d][a-z\\d\\-]{0,%d}", ENGINE_VERSION_ID_MAX_LENGTH - 1);

  private static final String MAJOR_VERSION_RE =
      String.format("(?:(?:(%s):)?)(%s)", ENGINE_ID_RE, ENGINE_VERSION_ID_RE);

  private static final String FULL_VERSION_RE =
      String.format("(%s)(\\.(\\d{1,%d}))?",  MAJOR_VERSION_RE, MINOR_VERSION_ID_LENGTH);

  private static final Pattern FULL_VERSION_PATTERN = Pattern.compile(FULL_VERSION_RE);

  private final String versionId;
  private final String majorVersion;
  private final String engineId;
  private final String engineVersionId;
  private final String minorVersion;

  /**
   * Create a new VersionId based on an versionId formatted as follows:
   *
   * [(engine):](engine version)[.(minor version)]
   *
   * @param versionId The versionId to parse.
   */
  private VersionId(String versionId) {
    if (versionId == null) {
      throw new NullPointerException();
    }
    Matcher matcher = FULL_VERSION_PATTERN.matcher(versionId);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Malformed versionId: " + versionId);
    }
    this.versionId = versionId;
    this.majorVersion = matcher.group(1);
    this.engineId = matcher.group(2) == null ? DEFAULT_ENGINE_ID : matcher.group(2);
    this.engineVersionId = matcher.group(3);
    this.minorVersion = matcher.group(5);
  }

  /**
   * @return the majorVersion
   */
  public String getMajorVersion() {
    return majorVersion;
  }

  /**
   * @return the minorVersion or {@code null} if no minor version was present.
   */
  public String getMinorVersion() {
    return minorVersion;
  }

  /**
   * @return the serverId
   */
  public String getEngineId() {
    return engineId;
  }

  /**
   * @return the serverId
   */
  public String getEngineVersionId() {
    return engineVersionId;
  }
  /**
   * @return the versionId
   */
  public String getVersionId() {
    return versionId;
  }

  public static VersionId parse(String versionId) {
    return new VersionId(versionId);
  }
}
