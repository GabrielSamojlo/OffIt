package com.gabrielsamojlo.offit.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class OffItInterceptor implements Interceptor {

    private String responseJson;
    private Call<?> call;
    private int responseCode;
    private int responseTime;

    public OffItInterceptor(Call<?> call, String responseJson, int responseCode, int responseTime) {
        this.responseJson = responseJson;
        this.call = call;
        this.responseCode = responseCode;
        this.responseTime = responseTime;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Thread.sleep(responseTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new okhttp3.Response.Builder()
                .code(responseCode)
                .message("OffIt")
                .protocol(Protocol.HTTP_2)
                .request(call.request())
                .body(ResponseBody.create(MediaType.parse("application/json"), responseJson)).build();
    }
}
