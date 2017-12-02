package com.gabrielsamojlo.offit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gabrielsamojlo.offit.annotations.Mockable;
import com.gabrielsamojlo.offit.interceptors.OffItInterceptor;
import com.gabrielsamojlo.offit.utils.CallUtils;
import com.gabrielsamojlo.offit.utils.ChainAsyncTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.internal.http.RealInterceptorChain;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MockedCall<T> implements com.gabrielsamojlo.offit.Call<T> {

    private Type type;

    private Context context;

    private Mockable[] mockableAnnotations;
    private Mockable selectedAnnotation;
    private Call<T> call;

    private String pathToJson = "";
    private int responseCode = 200;
    private int responseTime = 0;

    private List<Interceptor> interceptors;

    MockedCall(Type type, Call<T> call, Context context, Mockable[] mockableAnnotations, List<Interceptor> interceptors) {
        this.type = type;
        this.context = context;
        this.call = call;
        this.mockableAnnotations = mockableAnnotations;
        this.interceptors = new ArrayList<>(interceptors);

        // getting default values to be sure when no tag is specified
        selectedAnnotation = CallUtils.getDefaultAnnotation(mockableAnnotations);
        setupValuesFromAnnotation();
    }

    private void setupValuesFromAnnotation() {
        pathToJson = selectedAnnotation.jsonPath();
        responseCode = selectedAnnotation.responseCode();
        responseTime = selectedAnnotation.responseTime();
    }

    @Override
    public com.gabrielsamojlo.offit.Call<T> withResponseTime(int responseTimeInMillis) {
        responseTime = responseTimeInMillis;
        return this;
    }

    @Override
    public com.gabrielsamojlo.offit.Call<T> withResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public com.gabrielsamojlo.offit.Call<T> withTag(String tag) {
        selectedAnnotation = CallUtils.getAnnotationByTag(tag, mockableAnnotations);
        setupValuesFromAnnotation();

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response execute() throws IOException {
        final String json = CallUtils.getJsonFromAssetsPath(context, pathToJson);
        interceptors.add(new OffItInterceptor(call, json, responseCode, responseTime));

        Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0, call.request());
        okhttp3.Response response = chain.proceed(call.request());

        Response callResponse;
        if (response.isSuccessful()) {
            callResponse = CallUtils.getSuccessResponse(json, type, response);
        } else {
            callResponse = CallUtils.getErrorResponse(json, response);
        }

        return callResponse;
    }

    @Override
    public void enqueue(@NonNull final Callback callback) {
        final String json = CallUtils.getJsonFromAssetsPath(context, pathToJson);
        interceptors.add(new OffItInterceptor(call, json, responseCode, responseTime));

        Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0, call.request());
        ChainAsyncTask.OnChainProceededListener listener = new ChainAsyncTask.OnChainProceededListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(okhttp3.Response response) {
                if (response == null) {
                    Log.e("OffIt", "Response from chain is null. Something wrong happened when proceeding. Please contact author.");
                    throw new NullPointerException("Response from chain == null. Please contact author to resolve this issue.");
                }

                if (response.isSuccessful()) {
                    callback.onResponse(MockedCall.this, CallUtils.getSuccessResponse(json, type, response));
                } else {
                    callback.onResponse(MockedCall.this, CallUtils.getErrorResponse(json, response));
                }
            }
        };

        ChainAsyncTask chainAsyncTask = new ChainAsyncTask(call.request(), chain, listener);
        chainAsyncTask.execute();
    }

    @Override
    public boolean isExecuted() {
        return call.isExecuted();
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Call<T> clone() {
        return call.clone();
    }

    @Override
    public Request request() {
        return call.request();
    }

}
