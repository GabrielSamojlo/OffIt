package com.gabrielsamojlo.offitsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gabrielsamojlo.offit.Call;
import com.gabrielsamojlo.offit.Callback;

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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts();
            }
        });
    }

    private void getPosts() {
        Call<List<Post>> call = mApiService.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
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
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("error", t.getMessage());
            }
        });
    }
}
