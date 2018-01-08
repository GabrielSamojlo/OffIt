package com.gabrielsamojlo.offit.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MockedCallExecutor {

    private static MockedCallExecutor sInstance;
    private Executor delegate;
    private MainThreadExecutor mainThreadExecutor;

    private MockedCallExecutor() {
        delegate = Executors.newFixedThreadPool(3);
        mainThreadExecutor = new MainThreadExecutor();
    }

    public static MockedCallExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new MockedCallExecutor();
        }

        return sInstance;
    }

    public void execute(Runnable runnable) {
        delegate.execute(runnable);
    }

    public void executeOnMainThread(Runnable runnable) {
        mainThreadExecutor.execute(runnable);
    }

    public void setDelegate(Executor executor) {
        if (executor == null) {
            this.delegate = Executors.newFixedThreadPool(3);
            return;
        }

        this.delegate = executor;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            handler.post(runnable);
        }
    }

}
