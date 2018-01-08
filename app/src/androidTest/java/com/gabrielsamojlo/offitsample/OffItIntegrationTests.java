package com.gabrielsamojlo.offitsample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.gabrielsamojlo.offit.Callback;
import com.gabrielsamojlo.offit.MockableCall;
import com.gabrielsamojlo.offit.OffIt;
import com.gabrielsamojlo.offit.runner.OffitTestRule;
import com.gabrielsamojlo.offit.utils.CallUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class OffItIntegrationTests {

    private ApiService mockApiService;
    private ApiService realApiService;
    private Context appContext;

    @Rule
    public TestRule offitRule = new OffitTestRule();

    private List<Post> jsonToPostsList(String json) {
        Type type = new TypeToken<List<Post>>(){}.getType();
        String mockJson = CallUtils.getJsonFromAssetsPath(appContext.getAssets(), json);

        return new Gson().fromJson(mockJson, type);
    }

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .build();

        mockApiService = OffIt.withContext(appContext).withRetrofit(retrofit).build(true).create(ApiService.class);
        realApiService = OffIt.withContext(appContext).withRetrofit(retrofit).build(false).create(ApiService.class);
    }

    @Test
    public void offItEnabled_customCallShouldBeUsedAsRegularOne() throws Exception {
        Call<List<Post>> call = mockApiService.getPostsWithCustomCall();
        assertNotNull(call);
    }

    @Test
    public void offItEnabled_regularCallShouldBeCastableToMockableCall() throws Exception {
        Call<List<Post>> call = mockApiService.getPostsWithRetrofitCall();
        MockableCall<List<Post>> castedCall = ((MockableCall<List<Post>>) call);

        assertEquals(castedCall, call);
    }

    @Test
    public void offItDisabled_customCallShouldBeUsedAsRegularOne() throws Exception {
        Call<List<Post>> call = realApiService.getPostsWithCustomCall();
        assertNotNull(call);
    }

    @Test
    public void offItDisabled_regularCallShouldBeCastableToMockableCall() throws Exception {
        Call<List<Post>> call = realApiService.getPostsWithRetrofitCall();
        MockableCall<List<Post>> castedCall = ((MockableCall<List<Post>>) call);

        assertEquals(castedCall, call);
    }

    @Test
    public void offItEnabled_retrofitCallShouldBeMocked() throws Exception {
        Call<List<Post>> call = mockApiService.getPostsWithRetrofitCall();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> responsePosts = response.body();
                List<Post> mockedPosts = jsonToPostsList("get_posts.json");

                assertEquals(responsePosts, mockedPosts);
            }

            @Override
            public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Test
    public void offItEnabled_customCallShouldBeMocked() throws Exception {
        com.gabrielsamojlo.offit.Call<List<Post>> call = mockApiService.getPostsWithCustomCall();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> posts = response.body();
                List<Post> mockedPosts = jsonToPostsList("get_posts.json");

                assertEquals(posts, mockedPosts);
            }

            @Override
            public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Test
    public void offItDisabled_retrofitCallShouldBeMocked() throws Exception {
        Call<List<Post>> call = realApiService.getPostsWithRetrofitCall();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> responsePosts = response.body();
                List<Post> mockedPosts = jsonToPostsList("get_posts.json");

                assertNotNull(responsePosts);
                assertNotEquals(responsePosts, mockedPosts);
            }

            @Override
            public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Test
    public void offItDisabled_customCallShouldBeMocked() throws Exception {
        com.gabrielsamojlo.offit.Call<List<Post>> call = realApiService.getPostsWithCustomCall();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> posts = response.body();
                List<Post> mockedPosts = jsonToPostsList("get_posts.json");

                assertNotNull(posts);
                assertNotEquals(posts, mockedPosts);
            }

            @Override
            public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Test
    public void offItEnabled_withTagMethodShouldReturnProperMessage() throws Exception {
        Call<List<Post>> call = mockApiService.getPostsWithRetrofitCall();
        ((MockableCall<List<Post>>) call).withTag("no_posts").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
                Assert.assertEquals(422, response.code());
            }

            @Override
            public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {

            }
        });
    }

}
