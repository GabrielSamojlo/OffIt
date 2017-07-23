package com.gabrielsamojlo.offitsample;

import android.content.Context;

import com.gabrielsamojlo.offit.OffIt;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static ApiService getApiService(Context context, boolean isMocked) {
        return OffIt.withContext(context).withRetrofitBuilder(getRetrofitBuilder()).build(isMocked).create(ApiService.class);
    }

    public static ApiService getRealApiService(Context context) {
        return getApiService(context, false);
    }

    public static ApiService getMockApiService(Context context) {
        return getApiService(context, true);
    }

}
