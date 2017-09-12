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

import com.appunite.websocket.rx.messages.RxObjectEvent;
import com.appunite.websocket.rx.rxobject.RxObjectWebSockets;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class SocketConnectionImpl implements SocketConnection {

    @Nonnull
    private final RxObjectWebSockets sockets;
    @Nonnull
    private final Scheduler scheduler;

    public SocketConnectionImpl(@Nonnull RxObjectWebSockets sockets, @Nonnull Scheduler scheduler) {
        this.sockets = sockets;
        this.scheduler = scheduler;
    }

    @Nonnull
    @Override
    public Observable<RxObjectEvent> connection() {
        return sockets.webSocketObservable()
                .retryWhen(repeatDuration(1, TimeUnit.SECONDS));
    }

    @Nonnull
    private Function<Observable<? extends Throwable>, ObservableSource<?>> repeatDuration(final long delay,
                                                                                          @Nonnull final TimeUnit timeUnit) {
        return observable -> observable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                        return Observable.timer(delay, timeUnit, scheduler);
                    }
                });
    }
}
