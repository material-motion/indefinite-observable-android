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
package com.google.android.indefinite.observable;

import android.support.annotation.Nullable;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.testing.SimulatedSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IndefiniteObservableTests {

  @Test
  public void passesCorrectInteger() {
    SimulatedSource<Integer, Observer<Integer>> source = new SimulatedSource<>();

    Subscription assertion = assertThatNextValuesWillBeEqualTo(source, 5);
    source.next(5); // Assert 5 == 5.
    assertion.unsubscribe();

    assertion = assertThatNextValuesWillBeEqualTo(source, 6);
    source.next(6); // Assert 6 == 5.
    assertion.unsubscribe();
  }

  @Test
  public void disconnectorCanBeNull() {
    Subscription subscription = new IndefiniteObservable<>(
      new IndefiniteObservable.Connector<Observer<?>>() {
        @Nullable
        @Override
        public IndefiniteObservable.Disconnector connect(Observer<?> observer) {
          return IndefiniteObservable.Disconnector.NO_OP;
        }
      }).subscribe(new Observer<Object>() {
      @Override
      public void next(Object value) {
      }
    });

    subscription.unsubscribe(); // Should not crash.
  }

  @Test
  public void deprecatedSubscriberAndUnsubscriber() {
    new IndefiniteObservable<>(new IndefiniteObservable.Subscriber<Observer<?>>() {
      @Nullable
      @Override
      public IndefiniteObservable.Disconnector subscribe(Observer<?> observer) {
        return new IndefiniteObservable.Unsubscriber() {
          @Nullable
          @Override
          public void unsubscribe() {
          }
        };
      }
    }).subscribe(new Observer<Object>() {
      @Override
      public void next(Object value) {
      }
    }).unsubscribe();
  }

  @Test
  public void canUnsubscribeTwice() {
    Subscription subscription = new SimulatedSource<>().getObservable().subscribe(new Observer<Object>() {
      @Override
      public void next(Object value) {
      }
    });

    subscription.unsubscribe();
    subscription.unsubscribe();
  }

  private static <T> Subscription assertThatNextValuesWillBeEqualTo(
    SimulatedSource<T, Observer<T>> source, final T expected) {
    return source.getObservable().subscribe(new Observer<T>() {
      @Override
      public void next(T value) {
        assertThat(value).isEqualTo(expected);
      }
    });
  }
}
