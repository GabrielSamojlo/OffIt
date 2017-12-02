package com.gabrielsamojlo.offit;

/**
 * Created by Gabriel Samojło on 02.12.2017.
 */

public interface MockableCall<T> extends retrofit2.Call<T> {
    Call<T> withResponseTime(int responseTimeInMillis);

    Call<T> withResponseCode(int responseCode);

    Call<T> withTag(String tag);
}
