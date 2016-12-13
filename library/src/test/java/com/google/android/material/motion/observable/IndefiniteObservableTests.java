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

import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;

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
    Source<Integer> source = new Source<>();

    Subscription assertion = assertThatNextValuesWillBeEqualTo(source, 5);

    source.value = 5;
    source.next(); // Assert 5 == 5.

    assertion.unsubscribe();
    assertThatNextValuesWillBeEqualTo(source, 6);

    source.value = 6;
    source.next(); // Assert 6 == 5.
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
    Subscription subscription = new Source<>().getObservable().subscribe(new Observer<Object>() {
      @Override
      public void next(Object value) {
      }
    });

    subscription.unsubscribe();
    subscription.unsubscribe();
  }

  private static class Source<T> {

    public T value;
    private Observer<T> observer;

    public IndefiniteObservable<Observer<T>> getObservable() {
      return new IndefiniteObservable<>(
        new IndefiniteObservable.Connector<Observer<T>>() {

          @Nullable
          @Override
          public IndefiniteObservable.Disconnector connect(Observer<T> observer) {
            Source.this.observer = observer;

            return new IndefiniteObservable.Disconnector() {
              @Override
              public void disconnect() {
                Source.this.observer = null;
              }
            };
          }
        });
    }

    public void next() {
      if (observer != null) {
        observer.next(value);
      }
    }
  }

  private <T> Subscription assertThatNextValuesWillBeEqualTo(Source<T> source, final T expected) {
    return source.getObservable().subscribe(new Observer<T>() {
      @Override
      public void next(T value) {
        assertThat(value).isEqualTo(expected);
      }
    });
  }
}
