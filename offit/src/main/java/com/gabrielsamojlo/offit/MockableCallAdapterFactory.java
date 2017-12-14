package com.gabrielsamojlo.offit;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;


class MockableCallAdapterFactory extends CallAdapter.Factory {

    private static MockableCallAdapterFactory sInstance;
    private boolean isOffItTurnedOn;
    private AssetManager assetManager;
    private List<Interceptor> interceptors;

    public static MockableCallAdapterFactory getInstance(Context context, boolean isOffItTurnedOn) {
        if (sInstance == null || sInstance.isOffItTurnedOn() != isOffItTurnedOn) {
            sInstance = new MockableCallAdapterFactory(context, isOffItTurnedOn);
        }

        return sInstance;
    }

    private MockableCallAdapterFactory(Context context, boolean isOffItTurnedOn) {
        this.isOffItTurnedOn = isOffItTurnedOn;
        this.assetManager = context.getAssets();
    }

    boolean isOffItTurnedOn() {
        return isOffItTurnedOn;
    }

    void setFactory(okhttp3.Call.Factory factory) {
        if (factory instanceof OkHttpClient) {
            this.interceptors = ((OkHttpClient) factory).interceptors();
        }
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(@NonNull final Type returnType, @NonNull final Annotation[] annotations, @NonNull final Retrofit retrofit) {
        return new MockableCallAdapter<>(returnType, retrofit, annotations, this);
    }

    AssetManager getAssetManager() {
        return assetManager;
    }

    List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
