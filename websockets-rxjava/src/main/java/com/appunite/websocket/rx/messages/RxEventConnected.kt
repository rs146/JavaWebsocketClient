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

import com.appunite.websocket.rx.rxobject.ObjectWebSocketSender
import okhttp3.WebSocket

/**
 * Event indicate that client was connected to the server
 */
class RxEventConnected(sender: WebSocket): RxEventConn(sender) {

    override fun toString(): String = "ConnectedRxEvent{}"
}

/**
 * Event indicate that client was connected to the server
 *
 * @see RxEventConnected
 */
class RxObjectEventConnected(sender: ObjectWebSocketSender): RxObjectEventConn(sender) {

    override fun toString():String = "RxJsonEventConnected{}"
}
