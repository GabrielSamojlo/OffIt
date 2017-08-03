package com.gabrielsamojlo.offitsample;

import com.gabrielsamojlo.offit.Call;
import com.gabrielsamojlo.offit.Mockable;
import com.gabrielsamojlo.offit.Mockables;


import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/posts")
    @Mockables({
            @Mockable(tag = "success", responseCode = 200, jsonPath = "get_posts.json", responseTime = 350),
            @Mockable(tag = "no_posts", responseCode = 422, jsonPath = "get_posts_error.json", responseTime = 450)})
    Call<List<Post>> getPosts();


    @PUT("/posts/{id}")
    @Mockable(responseCode = 200, jsonPath = "update_post.json", responseTime = 3500)
    Call<Post> updatePost(@Body Post post, @Path("id") int postId);

}
