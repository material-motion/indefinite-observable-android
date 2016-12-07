/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion.observable;

/**
 * An observer receives new values from upstream and forwards them to downstream.
 * <p>
 * Upstream forwards new values to downstream by invoking {@link #next(Object)} when they are
 * produced. Implementations can have additional methods that can forward other values.
 * <p>
 * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
 *
 * @param <T> The type of value passed through the default {@link #next(Object)} method.
 */
public interface Observer<T> {

  /**
   * The default method that handles new values from upstream.
   */
  void next(T value);
}
