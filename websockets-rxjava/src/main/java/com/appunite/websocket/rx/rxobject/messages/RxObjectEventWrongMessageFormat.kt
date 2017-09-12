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

package com.appunite.websocket.rx.rxobject.messages;

import com.appunite.websocket.rx.messages.RxObjectEventConn
import com.appunite.websocket.rx.rxobject.ObjectParseException;
import com.appunite.websocket.rx.rxobject.ObjectSerializer;
import com.appunite.websocket.rx.rxobject.ObjectWebSocketSender;
import java.util.*

import javax.annotation.Nonnull;

/**
 * Event indicating that data returned by server was not correctly parsed
 *
 * This means {@link ObjectParseException} was returned via {@link ObjectSerializer}
 */
abstract class RxObjectEventWrongMessageFormat(sender: ObjectWebSocketSender, val exception: ObjectParseException): RxObjectEventConn(sender)

class RxObjectEventWrongBinaryMessageFormat(sender: ObjectWebSocketSender, private val message: ByteArray, exception: ObjectParseException):
        RxObjectEventWrongMessageFormat(sender, exception) {

    override fun toString(): String = "RxJsonEventWrongBinaryMessageFormat{message='${Arrays.toString(message)}'}"
}

class RxObjectEventWrongStringMessageFormat(sender: ObjectWebSocketSender, private val message: String, exception: ObjectParseException):
        RxObjectEventWrongMessageFormat(sender, exception) {

    override fun toString(): String = "RxJsonEventWrongStringMessageFormat{message='$message'}"
}


