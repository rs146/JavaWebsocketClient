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

package com.appunite.websocket.rx;

import com.appunite.websocket.rx.messages.RxEvent;
import com.appunite.websocket.rx.messages.RxEventBinaryMessage;
import com.appunite.websocket.rx.messages.RxEventConnected;
import com.appunite.websocket.rx.messages.RxEventDisconnected;
import com.appunite.websocket.rx.messages.RxEventStringMessage;

import javax.annotation.Nonnull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * This class allows to retrieve messages from websocket
 */
public class RxWebSockets {

    @Nonnull
    private final OkHttpClient client;
    @Nonnull
    private final Request request;

    /**
     * Create instance of {@link RxWebSockets}
     * @param client {@link OkHttpClient} instance
     * @param request request to connect to websocket
     */
    public RxWebSockets(@Nonnull OkHttpClient client, @Nonnull Request request) {
        this.client = client;
        this.request = request;
    }

    /**
     * Returns observable that connected to a websocket and returns {@link com.appunite.websocket.rx.messages.RxObjectEvent}'s
     *
     * @return Observable that connects to websocket
     */
    @Nonnull
    public Flowable<RxEvent> webSocketFlowable() {
        return Flowable.create(new FlowableOnSubscribe<RxEvent>() {

            @Override
            public void subscribe(@NonNull FlowableEmitter<RxEvent> flowableEmitter) throws Exception {
                final WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        flowableEmitter.onNext(new RxEventConnected(webSocket));
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        flowableEmitter.onNext(new RxEventStringMessage(webSocket, text));
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        flowableEmitter.onNext(new RxEventBinaryMessage(webSocket, bytes.toByteArray()));
                    }

                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                        super.onClosing(webSocket, code, reason);
                        final ServerRequestedCloseException exception = new ServerRequestedCloseException(code, reason);
                        flowableEmitter.onNext(new RxEventDisconnected(exception));
                        if (!flowableEmitter.isCancelled()) {
                            flowableEmitter.onError(exception);
                        }
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        final ServerRequestedCloseException exception = new ServerRequestedCloseException(code, reason);
                        flowableEmitter.onNext(new RxEventDisconnected(exception));
                        flowableEmitter.onComplete();
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        if (response != null) {
                            final ServerHttpError exception = new ServerHttpError(response);
                            flowableEmitter.onNext(new RxEventDisconnected(exception));
                            flowableEmitter.onError(exception);
                        } else {
                            flowableEmitter.onNext(new RxEventDisconnected(t));
                            flowableEmitter.onError(t);
                        }
                    }
                });
                flowableEmitter.setDisposable(new Disposable() {
                    volatile boolean disposed;

                    @Override
                    public void dispose() {
                        webSocket.close(1000, "Just disconnect");
                        flowableEmitter.onComplete();
                        disposed = true;
                    }

                    @Override
                    public boolean isDisposed() {
                        return disposed;
                    }
                });
            }
        }, BackpressureStrategy.LATEST);
    }

}
