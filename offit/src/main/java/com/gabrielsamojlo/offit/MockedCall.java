package com.gabrielsamojlo.offit;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MockedCall<T> implements com.gabrielsamojlo.offit.Call<T> {

    private Type type;
    private Context context;
    private NetworkSimulator networkSimulator;

    private Mockable[] mockableAnnotations;
    private Mockable selectedAnnotation;

    private MockedCall<T> instance;
    private Call<T> call;

    private String pathToJson = "";
    private int responseCode = 200;
    private int responseTime = 0;

    MockedCall(Type type, Call<T> call, Context context, Mockable[] mockableAnnotations, NetworkSimulator networkSimulator) {
        this.type = type;
        this.context = context;
        this.call = call;
        this.mockableAnnotations = mockableAnnotations;
        this.networkSimulator = networkSimulator;
        this.instance = this;

        // getting default values to be sure when no tag is specified
        selectedAnnotation = getDefaultAnnotation();
        setupValuesFromAnnotation();

        // informing if network simulator is set but only one annotation is present
        if (networkSimulator != null && mockableAnnotations.length == 1) {
            Log.e("OffIt", "Network simulator is turned on but only one annotation is present. Are you sure you want to simulate network?");
        }
    }

    private Mockable getDefaultAnnotation() {
        if (mockableAnnotations != null && mockableAnnotations.length > 0) {
            return mockableAnnotations[0];
        }

        else throw new NullPointerException("Mockable or Mockables annotation not found! (null or empty list)");
    }

    private Mockable getSuccessMockable() {
        for (Mockable mockable : mockableAnnotations) {
            if (mockable.responseCode() >= 200 && mockable.responseCode() < 300) {
                return mockable;
            }
        }

        Log.e("OffIt", "Annotation with success callback was not found, using default one");
        return getDefaultAnnotation();
    }

    private Mockable getErrorMockable() {
        for (Mockable mockable : mockableAnnotations) {
            if (mockable.responseCode() >= 300) {
                return mockable;
            }
        }

        Log.e("OffIt", "Annotation with error callback was not found, using default one");
        return getDefaultAnnotation();
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
    public void enqueue(final Callback callback) {
        if (networkSimulator != null) {
            double successRandom = new Random().nextDouble();

            if (successRandom < networkSimulator.getSuccessChance()) {
                selectedAnnotation = getSuccessMockable();
            } else {
                double failureRandom = new Random().nextDouble();
                if (failureRandom < networkSimulator.getErrorToFailureRatio()) {
                    callback.onFailure(instance, networkSimulator.getDefaultFailureThrowable());
                    return;
                } else {
                    selectedAnnotation = getErrorMockable();
                    setupValuesFromAnnotation();
                }

            }
        }

        final String json = instance.loadJSONFromAsset(pathToJson);
        final Gson gson = new Gson();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onResponse(instance, Response.success(gson.fromJson(json, type)));
                } else {
                    callback.onResponse(instance, Response.error(responseCode, ResponseBody.create(MediaType.parse("application/json"), json)));
                }
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
