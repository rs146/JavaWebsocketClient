package com.appunite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.appunite.websocket.rx.messages.RxEventBinaryMessage;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel mainViewModel = new MainViewModel();

        Button button = (Button) findViewById(R.id.button);

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(mainViewModel.reactToWebSocket()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxEvent -> {
                    if (rxEvent instanceof RxEventBinaryMessage) {
                        RxEventBinaryMessage rxEventBinaryMessage = (RxEventBinaryMessage) rxEvent;
                        byte[] message = rxEventBinaryMessage.message();
                        String s1 = Arrays.toString(message);
                        String s2 = new String(message);
                        Log.d(getClass().getSimpleName(), s2);
                    }
                }, throwable -> {
                    Log.d(getClass().getSimpleName(), throwable.getMessage());
                }));

        button.setOnClickListener(view -> {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        });
    }
}
