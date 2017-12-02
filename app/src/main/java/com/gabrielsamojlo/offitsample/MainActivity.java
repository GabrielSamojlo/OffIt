package com.gabrielsamojlo.offitsample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.gabrielsamojlo.offit.MockableCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiService = RestClient.getRealApiService(this);
        getPostsWithRetrofitCallUsingCasting();
    }

    private void getPostsWithRetrofitCall() {

        /*
        So we are using here regular retrofit call. As you can see, we can't access to the custom properties
        such as .withTag() and so on.

        When OffIt is enabled, default annotation (in this case this one with 'success' tag) will be
        executed. When OffIt is turned off, regular API Call will be executed.
         */

        Call<List<Post>> retrofitCall = mApiService.getPostsWithRetrofitCall();
        retrofitCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                // Same code you always use for handling API Response here
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable throwable) {
                // Same code you always use for handling API Response failure here
            }
        });
    }

    private void getPostsWithCustomCall() {

        /*
        Here we are using OffIt Call so we are able to access custom methods as .withTag().

        In this case, when OffIt is enabled, response with tag "no_posts" will be triggered.
        When we will disable OffIt, regular API Call will be executed.
         */

        com.gabrielsamojlo.offit.Call<List<Post>> offItCall = mApiService.getPostsWithCustomCall();
        offItCall.withTag("no_posts").enqueue(new com.gabrielsamojlo.offit.Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull com.gabrielsamojlo.offit.Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                // Same code you always use for handling API Response here
            }

            @Override
            public void onFailure(@NonNull com.gabrielsamojlo.offit.Call<List<Post>> call, @NonNull Throwable throwable) {
                // Same code you always use for handling API Response failure here
            }
        });
    }

    private void getPostsWithRetrofitCallUsingCasting() {

        /*
        There we are using regular retrofit call and after that, we are casting it to MockableCall.
        Thanks to that, we have access to custom methods like .withTag(), and we don't have to
        change our imports or add 'com.gabrielsamojlo.offit' prefixes.

        In this case, when OffIt is enabled, response with tag "no_posts" will be triggered.
        When we will disable OffIt, regular API Call will be executed.
         */

        Call<List<Post>> retrofitCall  = mApiService.getPostsWithRetrofitCall();
        ((MockableCall<List<Post>>) retrofitCall).withTag("no_posts").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                // Same code you always use for handling API Response here
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable throwable) {
                // Same code you always use for handling API Response failure here
            }
        });
    }
}
