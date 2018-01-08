package com.gabrielsamojlo.offit.utils;

import com.gabrielsamojlo.offit.interfaces.OnChainProceededListener;

/**
 * Created by Gabriel Samojło on 08.01.2018.
 */

public class ChainCallbackRunnable implements Runnable {
    private OnChainProceededListener listener;
    private okhttp3.Response response;

    public ChainCallbackRunnable(OnChainProceededListener listener) {
        this.listener = listener;
    }

    public void setResponse(okhttp3.Response response) {
        this.response = response;
    }

    @Override
    public void run() {
        if (response != null) {
            listener.onResponseReceived(response);
        }
    }
}
