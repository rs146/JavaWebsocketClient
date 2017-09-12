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

package com.appunite.websocket.rx.messages

/**
 * Event indicate that client was disconnected to the server
 *
 * since then all execution on previously returned {@link okhttp3.WebSocket} will cause throwing
 * {@link java.io.IOException}
 */
class RxEventDisconnected(val throwable: Throwable): RxEvent() {

    override fun toString(): String = "DisconnectedRxEvent{e=$throwable}"
}

class RxObjectEventDisconnected(private val throwable: Throwable): RxObjectEvent() {

    override fun toString(): String = "RxJsonEventDisconnected{exception=$throwable}"
}
