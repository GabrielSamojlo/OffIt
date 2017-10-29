package com.gabrielsamojlo.offit;

import android.content.Context;

import retrofit2.Retrofit;

public class OffIt {

   public static class Builder {
        private Retrofit.Builder retrofitBuilder;
        private Context context;
        private boolean isEnabled;

        Builder(Context context) {
            this.context = context;
            this.isEnabled = false;
        }

        public Retrofit build(boolean isEnabled) {
            this.isEnabled = isEnabled;

            return getModifiedRetrofit(isEnabled);
        }

        public Builder withRetrofitBuilder(Retrofit.Builder retrofit) {
            this.retrofitBuilder = retrofit;
            return this;
        }

        private Retrofit getModifiedRetrofit(boolean isEnabled) {
            MockableCallAdapterFactory factory = MockableCallAdapterFactory.getInstance(context, isEnabled);
            retrofitBuilder.addCallAdapterFactory(factory);

            Retrofit retrofit = retrofitBuilder.build();
            factory.setFactory(retrofit.callFactory());

            return retrofit;
        }

    }

    public static Builder withContext(Context context) {
        return new Builder(context);
    }

}
