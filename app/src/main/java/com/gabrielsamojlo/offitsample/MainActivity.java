package com.gabrielsamojlo.offitsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gabrielsamojlo.offit.MockableCall;
import com.gabrielsamojlo.offit.MockableCallback;

import java.util.List;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiService = RestClient.getMockApiService(this);
        MockableCall<List<Post>> call = mApiService.getPosts();
        call.enqueue(new MockableCallback<List<Post>>() {
            @Override
            public void onResponse(MockableCall<List<Post>> call, Response<List<Post>> response) {
                Log.e("size", String.valueOf(response.body().size()));
            }

            @Override
            public void onFailure(MockableCall<List<Post>> call, Throwable t) {

            }
        });
    }
}
