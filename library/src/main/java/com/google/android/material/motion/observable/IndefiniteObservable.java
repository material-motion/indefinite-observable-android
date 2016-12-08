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
 * Simple stream that synchronously dispatches "10", "11", "12":
 * <pre>
 * {@code
 * IndefiniteObservable<Observer<String>> observable = new IndefiniteObservable<>(
 *      new Subscriber<Observer<String>>() {
 *        public Unsubscriber subscribe(Observer<String> observer) {
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
 *      new Subscriber<Observer<Integer>>() {
 *        public Unsubscriber subscribe(Observer<Integer> observer) {
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
 * @param <O> The custom observer type that this IndefiniteObservable stream supports. See {@code
 * Observer<T>} above for an example.
 */
public class IndefiniteObservable<O extends Observer<?>> {

  private final Subscriber<O> subscriber;

  /**
   * Creates a new IndefiniteObservable with the given subscriber.
   * <p>
   * Accepts a subscriber that connects an observer to an external event source via {@link
   * Subscriber#subscribe(Observer)}.
   * <p>
   * {@link Subscriber#subscribe(Observer)} is only invoked when {@link #subscribe(Observer)} is
   * called.
   */
  public IndefiniteObservable(Subscriber<O> subscriber) {
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
   * @param observer The observer on which channel methods are called when new values are
   * produced.
   */
  public Subscription subscribe(O observer) {
    return new Subscription(subscriber.subscribe(observer));
  }

  /**
   * A subscriber provides the source of the stream.
   * <p>
   * When new values are produced, the subscriber should call {@link Observer#next(Object)} or
   * another appropriate method to send them downstream.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public static abstract class Subscriber<O extends Observer<?>> {

    /**
     * Connects an observer to an event source by calling {@link Observer#next(Object)} or
     * another appropriate method when new values are received.
     */
    @Nullable
    public abstract Unsubscriber subscribe(O observer);
  }

  /**
   * An unsubscriber tears down the source of the stream and releases any references made in
   * {@link Subscriber#subscribe(Observer)}.
   * <p>
   * {@link Observer#next(Object)} and all other methods should no longer be called after {@link
   * #unsubscribe()} is invoked.
   * <p>
   * See the class javadoc of {@link IndefiniteObservable} for an example implementation.
   */
  public static abstract class Unsubscriber {

    /**
     * Tears down the source of the stream.
     */
    public abstract void unsubscribe();
  }

  /**
   * A subscription is returned by {@link IndefiniteObservable#subscribe(Observer)} and allows you
   * to {@link #unsubscribe()} from the stream.
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
