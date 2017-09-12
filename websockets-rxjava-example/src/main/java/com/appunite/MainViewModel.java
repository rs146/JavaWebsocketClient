package com.appunite;

import com.appunite.websocket.rx.RxWebSockets;
import com.appunite.websocket.rx.messages.RxEvent;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class MainViewModel {

    Observable<RxEvent> reactToWebSocket() {
        final Request request = new Request.Builder()
                .get()
                .url("ws://54.171.181.77/feeds/race/")
                .build();

        return new RxWebSockets(new OkHttpClient(), request)
                .webSocketObservable();
    }
}
