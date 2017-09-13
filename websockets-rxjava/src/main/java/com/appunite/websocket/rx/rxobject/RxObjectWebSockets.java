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

package com.appunite.websocket.rx.rxobject;

import com.appunite.websocket.rx.RxWebSockets;
import com.appunite.websocket.rx.messages.RxEventBinaryMessage;
import com.appunite.websocket.rx.messages.RxObjectEvent;
import com.appunite.websocket.rx.messages.RxObjectEventConnected;
import com.appunite.websocket.rx.messages.RxObjectEventDisconnected;
import com.appunite.websocket.rx.rxobject.messages.RxObjectEventMessage;
import com.appunite.websocket.rx.messages.RxEvent;
import com.appunite.websocket.rx.messages.RxEventConnected;
import com.appunite.websocket.rx.messages.RxEventDisconnected;
import com.appunite.websocket.rx.messages.RxEventStringMessage;
import com.appunite.websocket.rx.rxobject.messages.RxObjectEventWrongBinaryMessageFormat;
import com.appunite.websocket.rx.rxobject.messages.RxObjectEventWrongStringMessageFormat;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import okhttp3.WebSocket;

import javax.annotation.Nonnull;

import okio.ByteString;

/**
 * This class allows to retrieve json messages from websocket
 */
public class RxObjectWebSockets {
    @Nonnull
    private final RxWebSockets rxWebSockets;
    @Nonnull
    private final ObjectSerializer objectSerializer;

    /**
     * Creates {@link RxObjectWebSockets}
     * @param rxWebSockets socket that is used to connect to server
     * @param objectSerializer that is used to parse messages
     */
    public RxObjectWebSockets(@Nonnull RxWebSockets rxWebSockets, @Nonnull ObjectSerializer objectSerializer) {
        this.rxWebSockets = rxWebSockets;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Returns observable that connected to a websocket and returns {@link RxObjectEvent}s
     *
     * @return Flowable that connects to websocket
     * @see RxWebSockets#webSocketFlowable()
     */
    @Nonnull
    public Flowable<RxObjectEvent> webSocketFlowable() {
        return rxWebSockets.webSocketFlowable()
                .lift(observer -> new FlowableSubscriber<RxEvent>() {

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onSubscribe(@NonNull Subscription subscription) {
                        observer.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(RxEvent rxEvent) {
                        if (rxEvent instanceof RxEventConnected) {
                            observer.onNext(new RxObjectEventConnected(jsonSocketSender(((RxEventConnected) rxEvent).getSender())));
                        } else if (rxEvent instanceof RxEventDisconnected) {
                            observer.onNext(new RxObjectEventDisconnected(((RxEventDisconnected) rxEvent).getThrowable()));
                        } else if (rxEvent instanceof RxEventStringMessage) {
                            final RxEventStringMessage stringMessage = (RxEventStringMessage) rxEvent;
                            observer.onNext(parseMessage(stringMessage));
                        } else if (rxEvent instanceof RxEventBinaryMessage) {
                            final RxEventBinaryMessage binaryMessage = (RxEventBinaryMessage) rxEvent;
                            observer.onNext(parseMessage(binaryMessage));
                        } else {
                            throw new RuntimeException("Unknown message type");
                        }
                    }

                    private RxObjectEvent parseMessage(RxEventStringMessage stringMessage) {
                        final String message = stringMessage.getMessage();
                        final Object object;
                        try {
                            object = objectSerializer.serialize(message);
                        } catch (ObjectParseException e) {
                            return new RxObjectEventWrongStringMessageFormat(jsonSocketSender(stringMessage.getSender()), message, e);
                        }
                        return new RxObjectEventMessage(jsonSocketSender(stringMessage.getSender()), object);
                    }

                    private RxObjectEvent parseMessage(RxEventBinaryMessage binaryMessage) {
                        final byte[] message = binaryMessage.getMessage();
                        final Object object;
                        try {
                            object = objectSerializer.serialize(message);
                        } catch (ObjectParseException e) {
                            return new RxObjectEventWrongBinaryMessageFormat(jsonSocketSender(binaryMessage.getSender()), message, e);
                        }
                        return new RxObjectEventMessage(jsonSocketSender(binaryMessage.getSender()), object);
                    }
                });
    }

    @Nonnull
    private ObjectWebSocketSender jsonSocketSender(@Nonnull final WebSocket sender) {
        return message -> {
            if (objectSerializer.isBinary(message)) {
                return sender.send(ByteString.of(objectSerializer.deserializeBinary(message)));
            } else {
                return sender.send(objectSerializer.deserializeString(message));
            }
        };
    }
}
