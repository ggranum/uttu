/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.geoffgranum.uttu.core.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import org.apache.commons.lang3.StringUtils;

public class Verify {

  @NotNull
  public static <T> T equal(@Nullable T actual, @Nullable Object expected, @NotNull String message) {
    if(!Objects.equals(actual, expected)) {
      throw new IllegalArgumentException(message);
    }
    return actual;
  }

  @NotNull
  public static <T> T equal(@Nullable T actual,
                            @Nullable Object expected,
                            @NotNull Class<? extends RuntimeException> exceptionType,
                            @NotNull String message,
                            Object... messageArgs) {
    if(!Objects.equals(actual, expected)) {
      throw newException(message, exceptionType, messageArgs);
    }
    return actual;
  }

  /**
   * Check that minLength &lt;= stringLength &lt;= maxLength.
   * If minLength is zero the provided string argument may be null.
   *
   * @throws IllegalArgumentException if minLength &lt; length, maxLength &gt; length, or if minLength &gt; 0 and
   *                                  <code>string</code> is null.
   */
  @NotNull
  public static String hasLength(@Nullable String string, int minLength, int maxLength, @NotNull String message) {
    if(string != null) {
      hasMinLength(string, minLength, message);
      hasMaxLength(string, maxLength, message);
    } else if(minLength > 0) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  /**
   * Check that minLength &lt;= stringLength &lt;= maxLength.
   * If minLength is zero the provided string argument may be null.
   *
   * @throws IllegalArgumentException if minLength &lt; length, maxLength &gt; length, or if minLength &gt; 0 and
   *                                  {@code string} is null.
   */
  @NotNull
  public static String hasLength(@Nullable String string,
                                 int minLength,
                                 int maxLength,
                                 Class<? extends RuntimeException> exceptionType,
                                 String message,
                                 Object... messageArgs) {
    if(string != null) {
      hasMinLength(string, minLength, exceptionType, message, messageArgs);
      hasMaxLength(string, maxLength, exceptionType, message, messageArgs);
    } else if(minLength > 0) {
      throw new IllegalArgumentException(message);
    }

    return string;
  }

  @NotNull
  public static String hasMaxLength(@Nullable String string, int maxLength, @NotNull String message) {
    string = isNotEmpty(string, message);
    if(string.length() > maxLength) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  @NotNull
  public static String hasMaxLength(String string,
                                    int maxLength,
                                    Class<? extends RuntimeException> exceptionType,
                                    String message,
                                    Object... messageArgs) {
    if(string.length() > maxLength) {
      throw newException(message, exceptionType, messageArgs);
    }
    return string;
  }

  @NotNull
  public static String hasMinLength(@Nullable String string, int minLength, @NotNull String message) {
    if(string.length() < minLength) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  @NotNull
  public static String hasMinLength(@Null String string,
                                    int minLength,
                                    Class<? extends RuntimeException> exceptionType,
                                    String message,
                                    Object... messageArgs) {
    if(string.length() < minLength) {
      throw newException(message, exceptionType, messageArgs);
    }
    return string;
  }

  public static void isFalse(boolean value, @NotNull String message, Object... args) {
    if(value) {
      throw newException(message, IllegalArgumentException.class, args);
    }
  }

  public static void isFalse(boolean value,
                             Class<? extends RuntimeException> exceptionType,
                             String message,
                             Object... args) {
    if(value) {
      throw newException(message, exceptionType, args);
    }
  }

  @NotNull
  public static <T> T[] isNotEmpty(@Nullable T[] argument,
                                   Class<? extends RuntimeException> exceptionType,
                                   String message,
                                   Object... messageArgs) {
    if(argument == null || argument.length == 0) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument;
  }

  public static <T> Collection<T> isNotEmpty(@Nullable Collection<T> argument,
                                             Class<? extends RuntimeException> exceptionType,
                                             String message,
                                             Object... messageArgs) {
    if(argument == null || argument.isEmpty()) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument;
  }

  @NotNull
  public static <T> Iterable<T> isNotEmpty(@Nullable Iterable<T> argument,
                                           Class<? extends RuntimeException> exceptionType,
                                           String message,
                                           Object... messageArgs) {
    if(argument == null || !argument.iterator().hasNext()) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument;
  }

  @NotNull
  public static String isNotEmpty(String argument,
                                  Class<? extends RuntimeException> exceptionType,
                                  String message,
                                  Object... messageArgs) {
    if(StringUtils.isEmpty(argument)) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument;
  }

  @NotNull
  public static String isNotEmpty(@Nullable String argument, @NotNull String message) {
    if(StringUtils.isEmpty(argument)) {
      throw new IllegalArgumentException(message);
    }
    return argument;
  }

  @NotNull
  public static <T> T isNotEqual(@Nullable T actual, @Nullable Object expected, @NotNull String message) {
    if(Objects.equals(actual, expected)) {
      throw new IllegalArgumentException(message);
    }
    return actual;
  }

  @NotNull
  public static String isNotEqual(String actual, String expected, @NotNull String message) {
    if(StringUtils.equalsIgnoreCase(actual, expected)) {
      throw new IllegalArgumentException(message);
    }
    return actual;
  }

  @NotNull
  public static <T> T isNotNull(@Nullable T argument, @NotNull String message) {
    if(argument == null) {
      throw new NullPointerException(message);
    }
    return argument;
  }

  @NotNull
  public static <T> T isNotNull(@Nullable T argument,
                                Class<? extends RuntimeException> exceptionType,
                                String message,
                                Object... messageArgs) {
    if(argument == null) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument;
  }

  public static void isNull(@Nullable Object argument, @NotNull String message) {
    if(argument != null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isNull(@Nullable Object argument,
                            Class<? extends RuntimeException> exceptionType,
                            String message,
                            Object... messageArgs) {
    if(argument != null) {
      throw newException(message, exceptionType, messageArgs);
    }
  }

  @NotNull
  public static <T> T isPresent(Optional<T> argument,
                                Class<? extends RuntimeException> exceptionType,
                                String message,
                                Object... messageArgs) {
    if(!argument.isPresent()) {
      throw newException(message, exceptionType, messageArgs);
    }
    return argument.get();
  }

  @NotNull
  public static boolean isTrue(boolean value, @NotNull String message, Object... args) {
    if(!value) {
      throw newException(message, IllegalArgumentException.class, args);
    }
    return value;
  }

  @NotNull
  public static boolean isTrue(boolean value,
                               Class<? extends RuntimeException> exceptionType,
                               String message,
                               Object... args) {
    if(!value) {
      throw newException(message, exceptionType, args);
    }
    //noinspection ConstantConditions
    return value;
  }

  @NotNull
  private static RuntimeException newException(@NotNull String message,
                                               @NotNull Class<? extends RuntimeException> exceptionType,
                                               Object... messageArgs) {
    RuntimeException e;
    message = String.format(message, messageArgs);
    if(exceptionType == null) {
      e = new IllegalArgumentException(message);
    } else {
      try {
        Constructor<? extends RuntimeException> constructor = exceptionType.getConstructor(String.class);
        e = constructor.newInstance(message);
      } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e1) {
        throw new RuntimeException("Exception Types provided to Preconditions must have a constructor " + "that takes a single string argument.",
                                   e1);
      }
    }
    return e;
  }
}
 
