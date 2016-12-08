# Observable implementation for Android

[![Build Status](https://travis-ci.org/material-motion/indefinite-observable-android.svg?branch=develop)](https://travis-ci.org/material-motion/indefinite-observable-android)
[![codecov](https://codecov.io/gh/material-motion/indefinite-observable-android/branch/develop/graph/badge.svg)](https://codecov.io/gh/material-motion/indefinite-observable-android)
[![Release](https://img.shields.io/github/release/material-motion/indefinite-observable-android.svg)](https://github.com/material-motion/indefinite-observable-android/releases/latest)
[![Docs](https://img.shields.io/badge/jitpack-docs-green.svg)](https://jitpack.io/com/github/material-motion/indefinite-observable-android/stable-SNAPSHOT/javadoc/)

The Observable implementation for Android repo.

Learn more about the APIs defined in the library by reading our
[technical documentation](https://jitpack.io/com/github/material-motion/indefinite-observable-android/2.0.0/javadoc/) and our
[Starmap](https://material-motion.github.io/material-motion/starmap/).

## Installation

### Installation with Jitpack

Add the Jitpack repository to your project's `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Depend on the [latest version](https://github.com/material-motion/indefinite-observable-android/releases) of the library.
Take care to occasionally [check for updates](https://github.com/ben-manes/gradle-versions-plugin).

```gradle
dependencies {
    compile 'com.github.material-motion:indefinite-observable-android:2.0.0'
}
```

For more information regarding versioning, see:

- [Material Motion Versioning Policies](https://material-motion.github.io/material-motion/team/essentials/core_team_contributors/release_process#versioning)

### Using the files from a folder local to the machine

You can have a copy of this library with local changes and test it in tandem
with its client project. To add a local dependency on this library, add this
library's identifier to your project's `local.dependencies`:

```
com.github.material-motion:indefinite-observable-android
```

> Because `local.dependencies` is never to be checked into Version Control
Systems, you must also ensure that any local dependencies are also defined in
`build.gradle` as explained in the previous section.

**Important**

For each local dependency listed, you *must* run `gradle install` from its
project root every time you make a change to it. That command will publish your
latest changes to the local maven repository. If your local dependencies have
local dependencies of their own, you must `gradle install` them as well.

You must `gradle clean` your project every time you add or remove a local
dependency.

### Usage

How to use the library in your project.

#### Editing the library in Android Studio

Open Android Studio,
choose `File > New > Import`,
choose the root `build.gradle` file.

## Example apps/unit tests

To build the sample application, run the following commands:

    git clone https://github.com/material-motion/indefinite-observable-android.git
    cd indefinite-observable-android
    gradle installDebug

To run all unit tests, run the following commands:

    git clone https://github.com/material-motion/indefinite-observable-android.git
    cd indefinite-observable-android
    gradle test

# Guides

1. [How to create a synchronous stream](#how-to-create-a-synchronous-stream)
1. [How to create an asynchronous stream using blocks](#how-to-create-an-asynchronous-stream-using-blocks)
1. [How to subscribe to a stream](#how-to-subscribe-to-a-stream)
1. [How to unsubscribe from a stream](#how-to-unsubscribe-from-a-stream)
1. [How to create an synchronous stream using objects](#how-to-create-an-synchronous-stream-using-objects)
1. [How to make a custom observable](#how-to-create-a-custom-observable)

## How to create a synchronous stream

```java
IndefiniteObservable<Observer<Integer>> observable = new IndefiniteObservable<>(
  new Subscriber<Observer<Integer>>() {
    @Nullable
    @Override
    public Unsubscriber subscribe(Observer<Integer> observer) {
      observer.next(5);
      return null;
    }
  });
}
```

## How to create an asynchronous stream using blocks

If you have an API that provides a callback-based mechanism for registering listeners then you can
create an asynchronous stream in place like so:

```java
IndefiniteObservable<Observer<Integer>> observable = new IndefiniteObservable<>(
  new Subscriber<Observer<Integer>>() {
   public Unsubscriber subscribe(Observer<Integer> observer) {
     final SomeToken token = registerSomeCallback(new SomeCallback() {
       public void onCallbackValue(Integer value) {
         observer.next(value);
       }
     });

     return new Unsubscriber() {
       public void unsubscribe() {
         unregisterSomeCallback(token);
       }
     };
   }
  });
```

## How to subscribe to a stream

```java
Subscription subscription = observable.subscribe(new Observer<Integer>() {
  public void next(Integer value) {
   Log.d(TAG, "Received value: " + value);
  }
});
```

## How to unsubscribe from a stream

```java
subscription.unsubscribe();
```

## How to make a custom observable

To create an observable that supports custom channels, extend Observer and IndefiniteObservable.

```java
public abstract class CustomObserver<T> extends Observer<T> {
  public abstract void next(T value);
  public abstract void customChannel(Foobar value);
}

public class CustomObservable<T> extends IndefiniteObservable<CustomObserver<T>> {
  public CustomObservable(Subscriber<CustomObserver<T>> subscriber) {
    super(subscriber);
  }
}
```

## Contributing

We welcome contributions!

Check out our [upcoming milestones](https://github.com/material-motion/indefinite-observable-android/milestones).

Learn more about [our team](https://material-motion.github.io/material-motion/team/),
[our community](https://material-motion.github.io/material-motion/team/community/), and
our [contributor essentials](https://material-motion.github.io/material-motion/team/essentials/).

## License

Licensed under the Apache 2.0 license. See LICENSE for details.
