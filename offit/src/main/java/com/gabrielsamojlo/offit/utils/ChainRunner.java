package com.gabrielsamojlo.offit.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by Gabriel Samojło on 08.01.2018.
 */

public class ChainRunner implements Runnable {
    private Interceptor.Chain chain;
    private Request request;
    private ChainCallbackRunnable callback;

    public ChainRunner(Interceptor.Chain chain, Request request, ChainCallbackRunnable callback) {
        this.chain = chain;
        this.request = request;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            final okhttp3.Response response = chain.proceed(request);
            callback.setResponse(response);

            MockedCallExecutor.getInstance().executeOnMainThread(callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
