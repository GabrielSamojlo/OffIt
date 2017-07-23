package com.gabrielsamojlo.offit;

import retrofit2.Response;

public abstract class Callback<T> implements retrofit2.Callback<T> {
    @Override
    public void onResponse(retrofit2.Call<T> call, Response<T> response) {
        if (call instanceof com.gabrielsamojlo.offit.Call) {
            onResponse((com.gabrielsamojlo.offit.Call<T>) call, response);
        } else throw new IllegalArgumentException("Call is not OffIt Call!");
    }

    @Override
    public void onFailure(retrofit2.Call<T> call, Throwable t) {
        if (call instanceof com.gabrielsamojlo.offit.Call) {
            onFailure((com.gabrielsamojlo.offit.Call<T>) call, t);
        } else throw new IllegalArgumentException("Call is not OffIt Call!");
    }

    public abstract void onResponse(com.gabrielsamojlo.offit.Call<T> call, Response<T> response);
    public abstract void onFailure(com.gabrielsamojlo.offit.Call<T> call, Throwable t);
}
