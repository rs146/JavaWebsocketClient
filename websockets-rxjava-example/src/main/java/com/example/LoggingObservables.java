/*
 * Copyright (C) 2015 Jacek Marchwicki <jacek.marchwicki@gmail.com>
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
 * limitations under the License
 */

package com.example;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class LoggingObservables {

    @Nonnull
    public static Subscriber<Object> logging(@Nonnull final Logger logger, @Nonnull final String tag) {
        return new Subscriber<Object>() {
            @Override
            public void onComplete() {
                logger.log(Level.INFO, tag + " - onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                logger.log(Level.SEVERE, tag + " - onError", e);
            }

            @Override
            public void onSubscribe(@NonNull Subscription subscription) {

            }

            @Override
            public void onNext(Object o) {
                logger.log(Level.INFO, tag + " - onNext: {0}", o == null ? "null" : o.toString());
            }
        };
    }

    @Nonnull
    public static Observer<Object> loggingOnlyError(@Nonnull final Logger logger, @Nonnull final String tag) {
        return new Observer<Object>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                logger.log(Level.SEVERE, tag + " - onError", e);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(Object o) {
            }
        };
    }
}
