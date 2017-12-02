package com.gabrielsamojlo.offitsample;

import com.gabrielsamojlo.offit.annotations.Mockable;
import com.gabrielsamojlo.offit.annotations.Mockables;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/posts")
    @Mockables({
            @Mockable(tag = "success", responseCode = 200, jsonPath = "get_posts.json", responseTime = 3500),
            @Mockable(tag = "no_posts", responseCode = 422, jsonPath = "get_posts_error.json", responseTime = 4500)})
    Call<List<Post>> getPostsWithRetrofitCall();

    @GET("/posts")
    @Mockables({
            @Mockable(tag = "success", responseCode = 200, jsonPath = "get_posts.json", responseTime = 3500),
            @Mockable(tag = "no_posts", responseCode = 422, jsonPath = "get_posts_error.json", responseTime = 4500)})
    com.gabrielsamojlo.offit.Call<List<Post>> getPostsWithCustomCall();

}
