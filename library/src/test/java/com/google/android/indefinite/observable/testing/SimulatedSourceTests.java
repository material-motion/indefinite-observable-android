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

import com.google.android.indefinite.observable.BuildConfig;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.Observer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SimulatedSourceTests {

  private SimulatedSource<Float, Observer<Float>> source;

  @Before
  public void setUp() {
    source = new SimulatedSource<>();
  }

  @Test
  public void passesValue() {
    Subscription assertion = assertThatNextValuesWillBeEqualTo(source, 5f);
    source.next(5f);
    assertion.unsubscribe();

    assertion = assertThatNextValuesWillBeEqualTo(source, 7f);
    source.next(7f);
    assertion.unsubscribe();
  }

  @Test
  public void canPassValuesWithNoObserver() {
    source.next(5f);
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
