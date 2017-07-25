package com.gabrielsamojlo.offit;

public interface Call<T> extends retrofit2.Call<T> {

    Call<T> withResponseTime(int responseTimeInMillis);
    Call<T> withResponseCode(int responseCode);
    Call<T> withTag(String tag);
}
