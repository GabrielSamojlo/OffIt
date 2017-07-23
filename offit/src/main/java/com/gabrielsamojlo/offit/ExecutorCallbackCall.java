package com.gabrielsamojlo.offit;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ExecutorCallbackCall<T> implements com.gabrielsamojlo.offit.Call<T> {

    final Executor callbackExecutor;
    final Call<T> delegate;

    private int counter = 0;

    ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
        this.callbackExecutor = callbackExecutor;
        this.delegate = delegate;
    }

    @Override public void enqueue(final Callback<T> callback) {
        delegate.enqueue(new Callback<T>() {
            @Override public void onResponse(Call<T> call, final Response<T> response) {
                callbackExecutor.execute(new Runnable() {
                    @Override public void run() {
                        if (delegate.isCanceled()) {
                            callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
                        } else {
                            callback.onResponse(ExecutorCallbackCall.this, response);
                        }
                    }
                });
            }

            @Override public void onFailure(Call<T> call, final Throwable t) {
                callbackExecutor.execute(new Runnable() {
                    @Override public void run() {
                        callback.onFailure(ExecutorCallbackCall.this, t);
                    }
                });
            }
        });
    }

    @Override public boolean isExecuted() {
        return delegate.isExecuted();
    }

    @Override public Response<T> execute() throws IOException {
        return delegate.execute();
    }

    @Override public void cancel() {
        delegate.cancel();
    }

    @Override public boolean isCanceled() {
        return delegate.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override public Call<T> clone() {
        return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
    }

    @Override public Request request() {
        return delegate.request();
    }

    @Override
    public com.gabrielsamojlo.offit.Call<T> withResponseTime(int responseTimeInMillis) {
        // no-op here
        return this;
    }

    @Override
    public com.gabrielsamojlo.offit.Call<T> withResponseCode(int responseCode) {
        // no-op here
        return this;
    }

}
