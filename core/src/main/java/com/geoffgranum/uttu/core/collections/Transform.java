/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.core.collections;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * @author ggranum
 */
public class Transform {

  public static <F, T> List<T> apply(List<F> from, Function<F, T> fn) {
    return from.stream().map(fn).collect(Collectors.toList());
  }

  public static <F, T> List<T> apply(F[] from, Function<F, T> fn) {
    return apply(Lists.newArrayList(from), fn);
  }

  public static <F, T> List<T> lazy(List<F> from, Function<F, T> fn) {
    return Lists.transform(from, new GoogleFnWrapper<>(fn));
  }

  public static <F, T> List<T> lazy(F[] from, Function<F, T> fn) {
    return lazy(Lists.newArrayList(from), fn);
  }

  private static class GoogleFnWrapper<F, T> implements com.google.common.base.Function<F,T> {

    private final Function<F,T> javaFn;

    public GoogleFnWrapper(Function<F,T> javaFn) {
      this.javaFn = javaFn;
    }

    @Nullable
    @Override
    public T apply(@Nullable F input) {
      return javaFn.apply(input);
    }
  }

}
