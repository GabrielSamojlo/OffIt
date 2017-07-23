package com.gabrielsamojlo.offitsample;

import com.gabrielsamojlo.offit.Call;
import com.gabrielsamojlo.offit.Mockable;


import java.util.List;

import retrofit2.http.GET;

public interface ApiService {

    @GET("/posts")
    @Mockable(responseCode = 200, jsonPath = "get_posts.json", responseTime = 3500)
    Call<List<Post>> getPosts();

}
