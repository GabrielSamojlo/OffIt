package com.gabrielsamojlo.offitsample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiService = RestClient.getRealApiService(this);
        com.gabrielsamojlo.offit.Call<List<Post>> call = mApiService.getPosts();
        call.withResponseTime(3000).enqueue(new retrofit2.Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                Log.e("responseCode", String.valueOf(response.code()));
                Log.e("isSuccessfull", String.valueOf(response.isSuccessful()));
                if (response.isSuccessful()) {
                    Log.e("size", String.valueOf(response.body().size()));
                } else {
                    try {
                        Log.e("error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Post>> call, Throwable t) {

            }
        });

    }
}
