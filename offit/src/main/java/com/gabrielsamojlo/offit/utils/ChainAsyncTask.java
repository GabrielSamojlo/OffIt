package com.gabrielsamojlo.offit.utils;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class ChainAsyncTask extends AsyncTask<Void, Void, okhttp3.Response> {

    public interface OnChainProceededListener {
        void onResponseReceived(okhttp3.Response response);
    }

    private OnChainProceededListener listener;
    private Interceptor.Chain chain;
    private Request request;

    public ChainAsyncTask(Request request, Interceptor.Chain chain, OnChainProceededListener listener) {
        this.listener = listener;
        this.chain = chain;
        this.request = request;
    }

    @Override
    protected okhttp3.Response doInBackground(Void... voids) {
        try {
            return chain.proceed(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(okhttp3.Response response) {
        super.onPostExecute(response);
        listener.onResponseReceived(response);
    }
}
