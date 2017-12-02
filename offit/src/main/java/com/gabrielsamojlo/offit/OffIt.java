package com.gabrielsamojlo.offit;

import android.content.Context;

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

            retrofitBuilder.addCallAdapterFactory(factory);
            factory.setFactory(retrofit.callFactory());

            retrofit = retrofitBuilder.build();

            return retrofit;
        }

    }

    public static Builder withContext(Context context) {
        return new Builder(context);
    }

}
