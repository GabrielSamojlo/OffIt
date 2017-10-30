package com.gabrielsamojlo.offitsample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gabrielsamojlo.offit.OffIt;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String s) {
                Log.d("Http", s);
            }
        });

        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logger).build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static ApiService getApiService(Context context, boolean isMocked) {
        return OffIt.withContext(context).withRetrofit(getRetrofit()).build(isMocked).create(ApiService.class);
    }

    public static ApiService getRealApiService(Context context) {
        return getApiService(context, false);
    }

    public static ApiService getMockApiService(Context context) {
        return getApiService(context, true);
    }

}
