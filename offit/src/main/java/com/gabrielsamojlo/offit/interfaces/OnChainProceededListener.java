package com.gabrielsamojlo.offit.interfaces;

import okhttp3.Response;

/**
 * Created by Gabriel Samojło on 08.01.2018.
 */

public interface OnChainProceededListener {
    void onResponseReceived(Response response);
}
