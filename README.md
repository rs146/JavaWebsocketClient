# JavaWebsocketClient also for Android
JavaWebsocketClient is library is simple library for Websocket connection in rx for java and Android.
It is designed to be fast and fault tolerant.

Currently we use okhttp3 for websocket connection because okhttp3 is simple and well tested solution.

## Content of the package

* Example websockets server [python twisted server](websockets-server/README.md)
* Rx-java websocket client library `websockets-rxjava/`
* Rx-java websocket android example `websockets-rxjava-example/`

## Reactive example

How to connect to server:

```java
final Request request = new Request.Builder()
        .get()
        .url("ws://10.10.0.2:8080/ws")
        .build();
final Disposable disposable = new RxWebSockets(new OkHttpClient(), request)
        .webSocketObservable()
        .subscribe(rxEvent -> System.out.println("Event: " + rxEvent.toString()));
Thread.sleep(10000);
disposable.dispose();
```

Send message on connected:

```java
final Subscription subscription = new RxWebSockets(newWebSocket, request)
        .webSocketObservable()
        .subscribe(new Action1<RxEvent>() {
            @Override
            public void call(RxEvent rxEvent) {
                if (rxEvent instanceof RxEventConnected) {
                    Observable.just("response")
                            .compose(RxMoreObservables.sendMessage((RxEventConnected) rxEvent))
                            .subscribe();
                }
            }
        });
Thread.sleep(1000);
subscription.unsubscribe();
```
