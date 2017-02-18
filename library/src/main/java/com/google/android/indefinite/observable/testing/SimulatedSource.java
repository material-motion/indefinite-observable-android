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
package com.google.android.indefinite.observable.testing;

import android.support.annotation.Nullable;

import com.google.android.indefinite.observable.IndefiniteObservable;
import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.Observer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A stream source for testing that allows you to emit value and monitor for them.
 * <p>
 * Emit new values using {@link #next(Object)} and monitor that they pass through by subscribing to
 * {@link #getObservable()}.
 */
public class SimulatedSource<T, O extends Observer<T>> {

  private final IndefiniteObservable<O> observable;
  protected Set<O> observers = new CopyOnWriteArraySet<>();

  public SimulatedSource() {
    observable = createObservable(
      new Connector<O>() {

        @Nullable
        @Override
        public Disconnector connect(final O observer) {
          observers.add(observer);

          return new Disconnector() {
            @Override
            public void disconnect() {
              observers.remove(observer);
            }
          };
        }
      });
  }

  protected IndefiniteObservable<O> createObservable(Connector<O> connector) {
    return new IndefiniteObservable<>(connector);
  }

  /**
   * Returns the stream so tests can subscribe to it and monitor the incoming values from the
   * stream.
   */
  public IndefiniteObservable<O> getObservable() {
    return observable;
  }

  /**
   * Emit the given value onto the stream.
   */
  public void next(T value) {
    for (Observer<T> observer : observers) {
      observer.next(value);
    }
  }
}
