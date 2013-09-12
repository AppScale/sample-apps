// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.appengine.api.datastore;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * {@link DatastoreCallbacks} implementation that knows how to parse a datastore
 * callbacks config file.
 *
 */
class DatastoreCallbacksImpl implements DatastoreCallbacks {

  static final String FORMAT_VERSION_PROPERTY = "DatastoreCallbacksFormatVersion";

  /**
   * The types of the callbacks we support.  There must be one enum value per
   * callback annotation, and each value must be the simple name of one of
   * these annotation classes.
   */
   enum CallbackType {
    /**
     * @see PrePut
     */
    PrePut(PutContext.class),

    /**
     * @see PostPut
     */
    PostPut(PutContext.class),

    /**
     * @see PreDelete
     */
    PreDelete(DeleteContext.class),

    /**
     * @see PostDelete
     */
    PostDelete(DeleteContext.class),

    /**
     * @see PreGet
     */
    PreGet(PreGetContext.class),

    /**
     * @see PostLoad
     */
    PostLoad(PostLoadContext.class),

    /**
     * @see PreQuery
     */
    PreQuery(PreQueryContext.class);

    final Class<? extends CallbackContext<?>> contextClass;
    final Class<? extends Annotation> annotationType;

    @SuppressWarnings("unchecked")
    CallbackType(Class<? extends CallbackContext<?>> contextClass) {
      this.contextClass = contextClass;
      try {
        this.annotationType = (Class<? extends Annotation>) Class.forName(
            getClass().getPackage().getName() + "." + this.name());
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  /**
   * Interface that we use to wrap a reflective call to the method that
   * actually implements the callback.
   */
  interface Callback {
    void run(CallbackContext<?> context);
  }

  /**
   * Key is the kind (possibly the empty string), value is a {@link Map} where
   * the key is the {@link CallbackType} and the value is a {@link List} of
   * {@link Callback Callbacks}.  Given a kind and a {@link CallbackType} we
   * can quickly navigate to a list of {@link Callback Callbacks} to run.
   */
  private final Map<CallbackType, Multimap<String, Callback>> callbacksByTypeAndKind =
      Maps.newHashMap();
  private final Multimap<CallbackType, Callback> noKindCallbacksByType =
      LinkedHashMultimap.create();

  /**
   * Constructs DatastoreCallbacksImpl from a config in the appropriate format.
   *
   * @param inputStream Provides the config.
   * @param ignoreMissingMethods If {@code true}, methods that are referenced
   * in the config that do not exist will be ignored.  If {@code false},
   * methods that are referenced in the config that do not exist will generate
   * an {@link InvalidCallbacksConfigException}.
   */
  DatastoreCallbacksImpl(InputStream inputStream, boolean ignoreMissingMethods) {
    if (inputStream == null) {
      throw new NullPointerException("inputStream must not be null");
    }
    Properties props = loadProperties(inputStream);

    if (!"1".equals(props.get(FORMAT_VERSION_PROPERTY))) {
      throw new IllegalArgumentException("Unsupported version for datastore callbacks config: " +
          props.get(FORMAT_VERSION_PROPERTY));
    }
    for (CallbackType callbackType : CallbackType.values()) {
      callbacksByTypeAndKind.put(callbackType, LinkedHashMultimap.<String, Callback>create());
    }
    for (String key : props.stringPropertyNames()) {
      if (!key.equals(FORMAT_VERSION_PROPERTY)) {
        processCallbackWithKey(key, props, ignoreMissingMethods);
      }
    }
  }

  /**
   * Loads a {@link Properties} object from the contents of the provided
   * {@link InputStream}.
   */
  private Properties loadProperties(InputStream inputStream) {
    Properties props = new Properties();
    try {
      props.loadFromXML(inputStream);
    } catch (IOException e) {
      throw new InvalidCallbacksConfigException(
          "Unable to read datastore callbacks config file.", e);
    }
    return props;
  }

  /**
   * Processes the callback in the provided {@link Properties} identified by the
   * provided {@code key}.
   */
  private void processCallbackWithKey(String key, Properties props, boolean ignoreMissingMethods) {

    String[] kindCallbackTypePair = key.split("\\.(?!.*\\.)");
    if (kindCallbackTypePair.length != 2) {
      throw new InvalidCallbacksConfigException(String.format(
          "Could not extract kind and callback type from '%s'", key));
    }
    String kind = kindCallbackTypePair[0];
    CallbackType callbackType;
    try {
      callbackType = CallbackType.valueOf(kindCallbackTypePair[1]);
    } catch (IllegalArgumentException iae) {
      throw new InvalidCallbacksConfigException(String.format(
          "Received unknown callback type %s", kindCallbackTypePair[1]));
    }
    String value = props.getProperty(key);

    for (String method : Splitter.on(',').trimResults().split(value)) {
      String[] classMethodPair = method.split(":");
      if (classMethodPair.length != 2) {
        throw new InvalidCallbacksConfigException(String.format(
            "Could not extract fully-qualified classname and method from '%s'", method));
      }
      addCallback(callbackType, kind, classMethodPair[0], classMethodPair[1], ignoreMissingMethods);
    }
  }

  private void addCallback(CallbackType callbackType, String kind, String className,
      String methodName, boolean ignoreMissingMethods) {
    Callback callback = newCallback(
        callbackType, className, methodName, callbackType.contextClass, ignoreMissingMethods);
    if (callback == null) {
      return;
    }
    if (kind.isEmpty()) {
      noKindCallbacksByType.put(callbackType, callback);
    } else {
      callbacksByTypeAndKind.get(callbackType).put(kind, callback);
    }
  }

  @Override
  public void executePrePutCallbacks(PutContext context) {
    executeCallbacks(CallbackType.PrePut, context);
  }

  @Override
  public void executePostPutCallbacks(PutContext context) {
    executeCallbacks(CallbackType.PostPut, context);
  }

  @Override
  public void executePreDeleteCallbacks(DeleteContext context) {
    executeCallbacks(CallbackType.PreDelete, context);
  }

  @Override
  public void executePostDeleteCallbacks(DeleteContext context) {
    executeCallbacks(CallbackType.PostDelete, context);
  }

  @Override
  public void executePreGetCallbacks(PreGetContext context) {
    executeCallbacks(CallbackType.PreGet, context);
  }

  @Override
  public void executePostLoadCallbacks(PostLoadContext context) {
    executeCallbacks(CallbackType.PostLoad, context);
  }

  @Override
  public void executePreQueryCallbacks(PreQueryContext context) {
    executeCallbacks(CallbackType.PreQuery, context);
  }

  private <T> void executeCallbacks(CallbackType callbackType, BaseCallbackContext<T> context) {
    context.executeCallbacks(
        callbacksByTypeAndKind.get(callbackType), noKindCallbacksByType.get(callbackType));
  }

  /**
   * Instantiates a callback of the appropriate type that, when executed,
   * invokes the method with the given name on the given object.
   *
   * @param className Fully-qualified name of the class with a method that
   * was annotated as a callback.
   * @param methodName The name of the annotated method.
   * @param contextClass The type of the single argument expected by the
   * annotated method.
   * @param ignoreMissingMethods If {@code true}, methods that are referenced
   * in the config that do not exist will be ignored.  If {@code false},
   * methods that are referenced in the config that do not exist will generate
   * an {@link InvalidCallbacksConfigException}.
   *
   * @return A {@link Callback} of the appropriate concrete type, or {@code null}
   * if {@code ignoreMissingMethods} is {@code true} and the method does not
   * exist.
   */
  private Callback newCallback(CallbackType callbackType, String className, String methodName,
      Class<? extends CallbackContext<?>> contextClass, boolean ignoreMissingMethods) {
    try {
      Class<?> cls = loadClass(className);
      Method m = cls.getDeclaredMethod(methodName, contextClass);
      m.setAccessible(true);
      if (m.getAnnotation(callbackType.annotationType) == null) {
        throw new InvalidCallbacksConfigException(String.format(
            "Unable to initialize datastore callbacks because method %s.%s(%s) is missing "
                + "annotation %s.",
            cls.getName(), methodName, contextClass.getName(), callbackType));
      }
      Object callbackImplementor = newInstance(cls);
      return allocateCallback(callbackImplementor, m);
    } catch (ClassNotFoundException e) {
      if (!ignoreMissingMethods) {
        throw new InvalidCallbacksConfigException(
            "Unable to initialize datastore callbacks due to missing class.", e);
      }
    } catch (NoSuchMethodException e) {
      if (!ignoreMissingMethods) {
        throw new InvalidCallbacksConfigException(
            "Unable to initialize datastore callbacks because of reference to missing method.", e);
      }
    }
    return null;
  }

  /**
   * Loads the class identified by the provided fully-qualified classname using
   * the current thread's context classloader.
   */
  private static Class<?> loadClass(String className) throws ClassNotFoundException {
    return Thread.currentThread().getContextClassLoader().loadClass(className);
  }

  /**
   * Constructs and returns an instance of the given class by locating and
   * invoking its no-arg constructor.
   */
  private static Object newInstance(Class<?> cls) {
    Constructor<?> ctor;
    try {
      ctor = cls.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new InvalidCallbacksConfigException(String.format(
          "Unable to initialize datastore callbacks because class %s does not have a no-arg "
          + "constructor.", cls.getName()), e);
    }
    ctor.setAccessible(true);
    try {
      return ctor.newInstance();
    } catch (Exception e) {
      throw new InvalidCallbacksConfigException(String.format(
          "Unable to initialize datastore callbacks due to exception received while constructing "
              + "an instance of %s", cls.getName()), e);
    }
  }

  private Callback allocateCallback(final Object callbackImplementor, final Method callbackMethod) {
    return new Callback() {
      @Override
      public void run(CallbackContext<?> context) {
        try {
          callbackMethod.invoke(callbackImplementor, context);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
          Throwables.propagateIfPossible(e.getCause());
          throw new RuntimeException("Callback method threw a checked exception.", e.getCause());
        }
      }
    };
  }

  static class InvalidCallbacksConfigException extends RuntimeException {
    InvalidCallbacksConfigException(String msg, Throwable throwable) {
      super(msg, throwable);
    }

    InvalidCallbacksConfigException(String msg) {
      super(msg);
    }
  }
}
