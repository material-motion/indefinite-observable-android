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
package com.google.android.material.motion.observable.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.observable.Observer;

/**
 * Observable implementation for Android sample Activity.
 */
public class MainActivity extends AppCompatActivity {

  private TextView text1;
  private TextView text2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);

    text1 = (TextView) findViewById(R.id.text1);
    text2 = (TextView) findViewById(R.id.text2);

    runDemo1();
    runDemo2();
  }

  private void runDemo1() {
    IndefiniteObservable<Observer<String>> observable = new IndefiniteObservable<>(
      new Connector<Observer<String>>() {
        @Nullable
        @Override
        public Disconnector connect(Observer<String> observer) {
          observer.next("foo");
          return null;
        }
      });

    Subscription subscription = observable.subscribe(new Observer<String>() {
      @Override
      public void next(String value) {
        text1.setText(value);
      }
    });

    subscription.unsubscribe();
  }

  private void runDemo2() {
    IndefiniteObservable<MultiChannelObserver<String, Integer>> observable = new IndefiniteObservable<>(
      new Connector<MultiChannelObserver<String, Integer>>() {
        @Nullable
        @Override
        public Disconnector connect(MultiChannelObserver<String, Integer> observer) {
          observer.next("bar");
          observer.customChannel(Color.RED);
          return null;
        }
      });

    Subscription subscription = observable.subscribe(new MultiChannelObserver<String, Integer>() {
      @Override
      public void next(String value) {
        text2.setText(value);
      }

      @Override
      public void customChannel(Integer value) {
        text2.setTextColor(value);
      }
    });

    subscription.unsubscribe();
  }

  /**
   * A multi-channel observer implementation.
   */
  public static abstract class MultiChannelObserver<T, U> extends Observer<T> {

    public abstract void next(T value);

    public abstract void customChannel(U value);
  }
}
