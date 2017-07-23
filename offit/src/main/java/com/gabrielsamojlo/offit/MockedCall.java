package com.gabrielsamojlo.offit;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MockedCall<T> implements com.gabrielsamojlo.offit.Call<T> {

    private Type type;

    private Context context;

    private Annotation annotation;
    private MockedCall<T> instance;
    private Call<T> call;

    private String pathToJson = "";
    private int responseCode = 200;
    private int responseTime = 0;

    MockedCall(Type type, Call<T> call, Context context, Annotation annotation) {
        this.type = type;
        this.context = context;
        this.call = call;
        this.annotation = annotation;
        this.instance = this;

        setupValuesFromAnnotation();
    }

    private void setupValuesFromAnnotation() {
        Mockable mockableAnnotation = (Mockable) annotation;

        pathToJson = mockableAnnotation.jsonPath();
        responseCode = mockableAnnotation.responseCode();
        responseTime = mockableAnnotation.responseTime();
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
    public com.gabrielsamojlo.offit.Call<T> additionalMethod() {
        return this;
    }

    @Override
    public Response execute() throws IOException {
        Gson gson = new Gson();
        final String json = instance.loadJSONFromAsset(pathToJson);

        return Response.success(gson.fromJson(json, type));
    }

    @Override
    public void enqueue(final Callback callback) {
        final String json = instance.loadJSONFromAsset(pathToJson);
        final Gson gson = new Gson();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(instance, Response.success(gson.fromJson(json, type)));
            }
        }, responseTime);
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }

}
