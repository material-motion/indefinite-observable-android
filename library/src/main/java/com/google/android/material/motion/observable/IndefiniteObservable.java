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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An IndefiniteObservable represents a sequence of values that may be observed.
 * <p>
 * IndefiniteObservable is meant for use with streams of values that have no concept of completion.
 * This is an implementation of a subset of the Observable interface defined at
 * http://reactivex.io/
 * <p>
 * Streams are not evaluated until {@link #subscribe(Observer)} is invoked.
 * <p>
 * Simple stream that synchronously dispatches "10", "11", "12":
 * <pre>
 * {@code
 * IndefiniteObservable<Observer<String>> observable = new IndefiniteObservable<>(
 *      new Connector<Observer<String>>() {
 *        public Disconnector connect(Observer<String> observer) {
 *          observer.next("10");
 *          observer.next("11");
 *          observer.next("12");
 *          return null;
 *        }
 *      });
 *
 *    Subscription subscription = observable.subscribe(new Observer<String>() {
 *      public void next(String value) {
 *        Log.d(TAG, "Received value: " + value);
 *      }
 *    });
 *
 *    subscription.unsubscribe();
 * }
 * </pre>
 * Simple stream that asynchronously forwards values from some callback:
 * <pre>
 * {@code
 * IndefiniteObservable<Observer<Integer>> observable = new IndefiniteObservable<>(
 *      new Connector<Observer<Integer>>() {
 *        public Disconnector connect(Observer<Integer> observer) {
 *          final SomeToken token = registerSomeCallback(new SomeCallback() {
 *            public void onCallbackValue(Integer value) {
 *              observer.next(value);
 *            }
 *          });
 *
 *          return new Disconnector() {
 *            public void disconnect() {
 *              unregisterSomeCallback(token);
 *            }
 *          };
 *        }
 *      });
 * }
 * </pre>
 *
 * @param <O> The custom observer type that this IndefiniteObservable stream supports. See {@code
 * Observer<T>} above for an example.
 */
public class IndefiniteObservable<O extends Observer<?>> {

  private final Connector<O> connector;

  /**
   * Creates a new IndefiniteObservable with the given connector.
   * <p>
   * Accepts a connector that connects an observer to an external event source via {@link
   * Connector#connect(Observer)}.
   * <p>
   * {@link Connector#connect(Observer)} is only invoked when {@link #subscribe(Observer)} is
   * called.
   */
  public IndefiniteObservable(Connector<O> connector) {
    this.connector = connector;
  }

  /**
   * Subscribes an observer to the IndefiniteObservable.
   * <p>
   * The observer will begin receiving values from the external event source.
   * <p>
   * To stop receiving values from the external event source, call {@link
   * Subscription#unsubscribe()} on the returned subscription.
   *
   * @param observer The observer on which channel methods are called when new values are
   * produced.
   */
  public Subscription subscribe(@NonNull O observer) {
    return new Subscription(connector.connect(observer));
  }

  /**
   * A connector provides the source of the stream by connecting an observer to an external event
   * source.
   * <p>
   * When new values are produced by the external event source, the connector should call {@link
   * Observer#next(Object)} or another appropriate method to send them downstream.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public static abstract class Connector<O extends Observer<?>> {

    /**
     * Connects the observer to an external event source. Ensures that {@link
     * Observer#next(Object)} and other appropriate methods are invoked when new values are
     * produced.
     *
     * @return A disconnector that disconnects the observer from the external event source.
     */
    @NonNull
    public abstract Disconnector connect(O observer);
  }

  /**
   * A disconnector tears down the source of the stream by disconnecting the observer from the
   * external event source.
   * <p>
   * {@link Observer#next(Object)} and all other methods should no longer be called after {@link
   * #disconnect()} is invoked.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public static abstract class Disconnector {

    /**
     * A disconnector that does nothing.
     */
    public static final Disconnector NO_OP = new Disconnector() {
      @Override
      public void disconnect() {
      }
    };

    /**
     * Disconnects the observer from the external event source.
     */
    public abstract void disconnect();
  }

  /**
   * @deprecated in #develop#. Use {@link Connector} instead.
   */
  @Deprecated
  public static abstract class Subscriber<O extends Observer<?>> extends Connector<O> {

    @Nullable
    public abstract Disconnector subscribe(O observer);

    @Nullable
    @Override
    public final Disconnector connect(O observer) {
      return subscribe(observer);
    }
  }

  /**
   * @deprecated in #develop#. Use {@link Disconnector} instead.
   */
  @Deprecated
  public static abstract class Unsubscriber extends Disconnector {

    @Nullable
    public abstract void unsubscribe();

    @Override
    public final void disconnect() {
      unsubscribe();
    }
  }

  /**
   * A subscription is returned by {@link IndefiniteObservable#subscribe(Observer)} and allows you
   * to {@link #unsubscribe()} from the stream.
   */
  public static final class Subscription {

    @Nullable
    private Disconnector disconnector;

    private Subscription(@NonNull Disconnector disconnector) {
      this.disconnector = disconnector;
    }

    /**
     * Unsubscribes from the stream.
     */
    public void unsubscribe() {
      if (disconnector != null) {
        disconnector.disconnect();
        disconnector = null;
      }
    }
  }
}
