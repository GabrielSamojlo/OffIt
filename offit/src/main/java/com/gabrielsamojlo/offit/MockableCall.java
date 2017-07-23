package com.gabrielsamojlo.offit;

import retrofit2.Call;

/**
 * Created by gabrielsamojlo on 20.07.2017.
 */

public interface MockableCall<T> extends Call<T> {

    MockableCall<T> additionalMethod();

}
