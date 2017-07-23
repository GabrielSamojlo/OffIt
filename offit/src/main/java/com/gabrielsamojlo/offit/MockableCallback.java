package com.gabrielsamojlo.offit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MockableCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (call instanceof MockableCall) {
            onResponse((MockableCall) call, response);
        } else throw new IllegalArgumentException("Call is not MockableCall!");
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (call instanceof MockableCall) {
            onFailure((MockableCall) call, t);
        } else throw new IllegalArgumentException("Call is not MockableCall!");
    }

    public abstract void onResponse(MockableCall<T> call, Response<T> response);
    public abstract void onFailure(MockableCall<T> call, Throwable t);
}
