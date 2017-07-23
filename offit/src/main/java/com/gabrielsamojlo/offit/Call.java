package com.gabrielsamojlo.offit;

public interface Call<T> extends retrofit2.Call<T> {

    Call<T> additionalMethod();

}
