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

package com.appunite.websocket.rx.rxobject.messages

import com.appunite.websocket.rx.messages.RxEventStringMessage
import com.appunite.websocket.rx.messages.RxObjectEventConn
import com.appunite.websocket.rx.rxobject.ObjectWebSocketSender
import io.reactivex.ObservableTransformer

@Suppress("UNCHECKED_CAST")
/**
 * Event indicating that data returned by server was parsed
 *
 * If {@link ObjectParseException} occur than {@link RxObjectEventWrongMessageFormat} event
 * will be served
 *
 * @see RxEventStringMessage
 */
class RxObjectEventMessage(sender: ObjectWebSocketSender, val message: Any) : RxObjectEventConn(sender) {

    override fun toString(): String = "RxJsonEventMessage{message=$message}"

    fun <T> message(): T = message as T

    /**
     * Transform one observable to observable of given type filtering by a type
     *
     * @param clazz type of message that you would like get
     * @param <T> type of message that you would like get
     * @return Observable that returns given type of message
     */
    fun <T> filterAndMap(clazz: Class<T>): ObservableTransformer<RxObjectEventMessage, T> {
        return ObservableTransformer { observable ->
            observable.filter { it -> clazz.isInstance(it.message)  }
                    .map(RxObjectEventMessage::message)
        }
    }
}
