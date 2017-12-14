package com.gabrielsamojlo.offit;

import android.content.Context;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class OffIt {

    public static class Builder {
        private Retrofit.Builder retrofitBuilder;
        private Context context;
        private Retrofit retrofit;

        Builder(Context context) {
            this.context = context;
        }

        public Retrofit build(boolean isEnabled) {
            return getModifiedRetrofit(isEnabled);
        }

        @Deprecated
        public Builder withRetrofitBuilder(Retrofit.Builder retrofit) {
            this.retrofitBuilder = retrofit;
            return this;
        }

        public Builder withRetrofit(Retrofit retrofit) {
            this.retrofit = retrofit;
            this.retrofitBuilder = retrofit.newBuilder();

            return this;
        }

        private Retrofit getModifiedRetrofit(boolean isEnabled) {
            MockableCallAdapterFactory factory = MockableCallAdapterFactory.getInstance(context, isEnabled);

            List<CallAdapter.Factory> factories = new ArrayList<>();
            factories.add(factory);
            factories.addAll(retrofit.callAdapterFactories());

            try {
                FieldUtils.writeField(retrofitBuilder, "adapterFactories", factories, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            factory.setFactory(retrofit.callFactory());

            retrofit = retrofitBuilder.build();

            return retrofit;
        }

    }

    public static Builder withContext(Context context) {
        return new Builder(context);
    }

}
