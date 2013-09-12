package com.google.appengine.api.datastore;

import static com.google.appengine.api.datastore.DataTypeUtils.CheckValueOption.ALLOW_MULTI_VALUE;
import static com.google.appengine.api.datastore.DataTypeUtils.CheckValueOption.VALUE_PRE_CHECKED_WITHOUT_NAME;

import com.google.appengine.api.datastore.DataTypeUtils.CheckValueOption;
import com.google.apphosting.api.AppEngineInternal;

import java.util.EnumSet;
import java.util.Set;

/**
 * Internal class that provides utility methods for {@link ExtendableEntity} objects.
 *
 */
@AppEngineInternal
public final class ExtendableEntityUtil {

  /**
   * Creates a new {@code Key} with the provided parent and kind. The instantiated
   * {@Key} will be incomplete.
   *
   * @param parent the parent of the key to create, can be {@code null}
   * @param kind the kind of the key to create
   */
  public static Key createKey(Key parent, String kind) {
    return new Key(kind, parent);
  }

  /**
   * Check if the input {@code Key} objects are equal (including keys that are
   * incomplete).
   *
   * @param key1 the first input key
   * @param key2 the second input key
   * @return {@code true} if the keys are equal. {@code false} otherwise.
   */
  public static boolean areKeysEqual(Key key1, Key key2) {
    return key1 == null ? key2 == null : key1.equals(key2, false);
  }

  /**
   * If the specified object cannot be used as the value for a {@code Entity} property, throw
   * an exception with the appropriate explanation.
   *
   * @param propertyName the name of the property.
   * @param value value in question
   * @param supportedTypes the types considered to be valid types for the value.
   * @param valuePreChecked {@code true} if the value without the name has already been checked.
   *     {@code false} otherwise.
   * @throws IllegalArgumentException if the type is not supported, or if the object is in
   * some other way invalid.
   */
  public static void checkSupportedValue(String propertyName, Object value, boolean valuePreChecked,
      Set<Class<?>> supportedTypes) {
    EnumSet<CheckValueOption> options = EnumSet.of(ALLOW_MULTI_VALUE);
    if (valuePreChecked) {
      options.add(VALUE_PRE_CHECKED_WITHOUT_NAME);
    }
    DataTypeUtils.checkSupportedValue(propertyName, value, options, supportedTypes);
  }

  private ExtendableEntityUtil() {
  }
}
