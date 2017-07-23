package com.gabrielsamojlo.offitsample;

import com.gabrielsamojlo.offit.Mockable;
import com.gabrielsamojlo.offit.MockableCall;

import java.util.List;

import retrofit2.http.GET;

public interface ApiService {

    @GET("/posts")
    @Mockable(responseCode = 200, jsonPath = "get_posts.json", responseTime = 3500)
    MockableCall<List<Post>> getPosts();

}
