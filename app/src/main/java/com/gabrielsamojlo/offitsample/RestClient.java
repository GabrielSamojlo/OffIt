package com.gabrielsamojlo.offitsample;

import android.content.Context;
import android.util.Log;

import com.gabrielsamojlo.offit.OffIt;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit.Builder getRetrofitBuilder() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("Log", message);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
        return new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static ApiService getApiService(Context context, boolean isMocked) {
        return OffIt.withContext(context).withRetrofitBuilder(getRetrofitBuilder()).withNetworkSimulator().build(isMocked).create(ApiService.class);
    }

    public static ApiService getRealApiService(Context context) {
        return getApiService(context, false);
    }

    public static ApiService getMockApiService(Context context) {
        return getApiService(context, true);
    }

}
