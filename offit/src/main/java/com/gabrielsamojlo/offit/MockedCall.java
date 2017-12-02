package com.gabrielsamojlo.offit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gabrielsamojlo.offit.annotations.Mockable;
import com.gabrielsamojlo.offit.interceptors.OffItInterceptor;
import com.gabrielsamojlo.offit.utils.ChainAsyncTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealInterceptorChain;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MockedCall<T> implements com.gabrielsamojlo.offit.Call<T> {

    private Type type;

    private Context context;

    private Mockable[] mockableAnnotations;
    private Mockable selectedAnnotation;

    private MockedCall<T> instance;
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
        this.instance = this;
        this.interceptors = new ArrayList<>(interceptors);

        // getting default values to be sure when no tag is specified
        selectedAnnotation = getDefaultAnnotation();
        setupValuesFromAnnotation();
    }

    private Mockable getDefaultAnnotation() {
        if (mockableAnnotations != null && mockableAnnotations.length > 0) {
            return mockableAnnotations[0];
        }

        else throw new NullPointerException("Mockable or Mockables annotation not found! (null or empty list)");
    }

    private Mockable getAnnotationByTag(String tag) {
        for (Mockable mockable : mockableAnnotations) {
            if (mockable.tag().equals(tag)) {
                return mockable;
            }
        }

        Log.e("OffIt", "Annotation with tag " + tag + " was not found, using default one");
        return getDefaultAnnotation();
    }

    private void setupValuesFromAnnotation() {
        pathToJson = selectedAnnotation.jsonPath();
        responseCode = selectedAnnotation.responseCode();
        responseTime = selectedAnnotation.responseTime();
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
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
        selectedAnnotation = getAnnotationByTag(tag);
        setupValuesFromAnnotation();

        return this;
    }

    @Override
    public Response execute() throws IOException {
        Gson gson = new Gson();
        final String json = instance.loadJSONFromAsset(pathToJson);

        return Response.success(gson.fromJson(json, type));
    }

    @Override
    public void enqueue(@NonNull final Callback callback) {
        final String json = instance.loadJSONFromAsset(pathToJson);
        final Gson gson = new Gson();
        interceptors.add(new OffItInterceptor(call, json, responseCode, responseTime));

        Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0, call.request());

        ChainAsyncTask.OnChainProceededListener listener = new ChainAsyncTask.OnChainProceededListener() {
            @Override
            public void onResponseReceived(okhttp3.Response response) {
                if (response == null) {
                    Log.e("OffIt", "Response from chain is null. Something wrong happened when proceeding. Please contact author.");
                    throw new NullPointerException("Response from chain == null. Please contact author to resolve this issue.");
                }

                if (response.isSuccessful()) {
                    callback.onResponse(instance, Response.success(gson.fromJson(json, type), response));
                } else {
                    callback.onResponse(instance, Response.error(ResponseBody.create(MediaType.parse("application/json"), json), response));
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
