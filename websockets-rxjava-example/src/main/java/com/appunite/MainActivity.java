package com.appunite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.appunite.websocket.rx.SchedulerProvider;
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

        SchedulerProvider schedulerProvider = new SchedulerProvider(Schedulers.io(), AndroidSchedulers.mainThread());

        compositeDisposable.add(mainViewModel.reactToWebSocket()
                .compose(schedulerProvider.getFlowableSchedulersTransformer())
                .subscribe(rxEvent -> {
                    if (rxEvent instanceof RxEventBinaryMessage) {
                        RxEventBinaryMessage rxEventBinaryMessage = (RxEventBinaryMessage) rxEvent;
                        byte[] message = rxEventBinaryMessage.getMessage();
                        String s1 = Arrays.toString(message);
                        String s2 = new String(message);
                        Log.d(getClass().getSimpleName(), s2);
                    }
                }, throwable -> Log.d(getClass().getSimpleName(), throwable.getMessage())));

        button.setOnClickListener(view -> {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        });
    }
}
