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

import android.support.annotation.Nullable;

/**
 * An IndefiniteObservable represents a sequence of values that may be observed.
 * <p>
 * IndefiniteObservable is meant for use with streams of values that have no concept of completion.
 * This is an implementation of a subset of the Observable interface defined at
 * http://reactivex.io/
 * <p>
 * Simple stream that synchronously dispatches 10, 11, 12:
 * <pre>
 * {@code
 * IndefiniteObservable<Integer> observable = new IndefiniteObservable<>(
 *      new Subscriber<Integer>() {
 *        public Unsubscriber subscribe(ValueObserver<Integer> observer) {
 *          observer.next(10);
 *          observer.next(11);
 *          observer.next(12);
 *          return null;
 *        }
 *      });
 *
 *    Subscription subscription = observable.subscribe(new ValueObserver<Integer>() {
 *      public void next(Integer value) {
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
 * IndefiniteObservable<Integer> observable = new IndefiniteObservable<>(
 *      new Subscriber<Integer>() {
 *        public Unsubscriber subscribe(ValueObserver<Integer> observer) {
 *          final SomeToken token = registerSomeCallback(new SomeCallback() {
 *            public void onCallbackValue(Integer value) {
 *              observer.next(value);
 *            }
 *          });
 *
 *          return new Unsubscriber() {
 *            public void unsubscribe() {
 *              unregisterSomeCallback(token);
 *            }
 *          };
 *        }
 *      });
 * }
 * </pre>
 *
 * @param <T> The type of values in this stream.
 */
public class IndefiniteObservable<T> {

  private final Subscriber<T> subscriber;

  /**
   * Creates a new IndefiniteObservable with the given subscriber.
   * <p>
   * Accepts a subscriber that connects an observer to an external event source via {@link
   * Subscriber#subscribe(ValueObserver)}.
   * <p>
   * {@link Subscriber#subscribe(ValueObserver)} is only invoked when {@link
   * #subscribe(ValueObserver)} is called.
   */
  public IndefiniteObservable(Subscriber<T> subscriber) {
    this.subscriber = subscriber;
  }

  /**
   * Subscribes an observer to the IndefiniteObservable.
   * <p>
   * The observer will begin receiving values from the external event source.
   * <p>
   * To stop receiving values from the external event source, call {@link
   * Subscription#unsubscribe()} on the returned subscription.
   *
   * @param observer The observer on which {@link ValueObserver#next(Object)} is called when new
   * values are produced.
   */
  public Subscription subscribe(ValueObserver observer) {
    return new Subscription(subscriber.subscribe(observer));
  }

  /**
   * A subscriber provides the source of the stream.
   * <p>
   * When new values are produced, the subscriber should call {@link ValueObserver#next(Object)}
   * to send them downstream.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public interface Subscriber<T> {

    /**
     * Connects an observer to an event source by calling {@link ValueObserver#next(Object)}
     * when new values are received.
     */
    @Nullable
    Unsubscriber subscribe(ValueObserver<T> observer);
  }

  /**
   * An unsubscriber tears down the source of the stream and releases any references made in
   * {@link Subscriber#subscribe(ValueObserver)}.
   * <p>
   * {@link ValueObserver#next(Object)} should no longer be called after {@link #unsubscribe()} is
   * invoked.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public interface Unsubscriber {

    /**
     * Tears down the source of the stream.
     */
    void unsubscribe();
  }

  /**
   * An observer receives new values from upstream and forwards them to downstream.
   * <p>
   * Upstream forwards new values to downstream by invoking {@link #next(Object)} when they are
   * produced.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public interface ValueObserver<T> {

    /**
     * When implementing, handles new values from upstream. When calling, forwards new values to
     * downstream.
     */
    void next(T value);
  }

  /**
   * A subscription is returned by {@link IndefiniteObservable#subscribe(ValueObserver)} and
   * allows you to {@link #unsubscribe()} from the stream.
   */
  public static final class Subscription {

    @Nullable
    private Unsubscriber unsubscriber;

    private Subscription(@Nullable Unsubscriber unsubscriber) {
      this.unsubscriber = unsubscriber;
    }

    /**
     * Unsubscribes from the stream.
     */
    public void unsubscribe() {
      if (unsubscriber != null) {
        unsubscriber.unsubscribe();
        unsubscriber = null;
      }
    }
  }
}
