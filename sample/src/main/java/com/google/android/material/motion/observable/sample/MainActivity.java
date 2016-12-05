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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.ValueObserver;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscriber;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.observable.IndefiniteObservable.Unsubscriber;

/**
 * Observable implementation for Android sample Activity.
 */
public class MainActivity extends AppCompatActivity {

  private TextView text;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);

    text = (TextView) findViewById(R.id.text);

    runDemo1();
  }

  private void runDemo1() {
    IndefiniteObservable<String> observable = new IndefiniteObservable<>(
      new Subscriber<String>() {
        @Nullable
        @Override
        public Unsubscriber subscribe(ValueObserver<String> observer) {
          observer.next("Payload");
          return null;
        }
      });

    Subscription subscription = observable.subscribe(new ValueObserver<String>() {
      @Override
      public void next(String value) {
        text.setText(value);
      }
    });

    subscription.unsubscribe();
  }
}
