# OffIt 
[ ![Download](https://api.bintray.com/packages/gabrielsamojlo/OffIt/OffIt/images/download.svg) ](https://bintray.com/gabrielsamojlo/OffIt/OffIt/_latestVersion)

### What is this?

OffIt is simple but powerful mocking library based on Retrofit. 

Do you want to build a demo of your app without worries about your backend reliability?
Or maybe you want to prototype your app quickly or just don't have any backend yet?
And what if you just want to quickly mock your Retrofit responses in your tests?

OffIt is for you!
Make mocks great again.

### How to download?

Simple add OffIt as a dependency in your app level ```build.gradle``` file.

```gradle
dependencies {
    compile 'com.gabrielsamojlo.offit:offit:1.0.0-rc'
}

```

You are ready to go!

### Configuration

First of all, you need to pass your ```Retrofit``` instance to Offit. Its really simple:

```java
 OffIt.withContext(context).withRetrofit(retrofit).build(isMocked).create(ApiService.class);
```

where ```retrofit``` is your good, old ```Retrofit``` you are using every day:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
```

### Mocking

OffIt gives you three ways of mocking your responses:

* The simple one is just annotating your API Interface without modifying rest of your codebase.

* The second, more powerful, is using modified classes. In this scenario, changes in your code are necessary.

* The third one is additional method based on casting your original Call to MockableCall


#### First way: Basic Mocking ####

Basic mocking is really simple. You need to use ```@Mockable``` annotation to tell OffIt how you want to mock your call :

```java
@GET("/posts/{id}")
@Mockable(responseCode = 200, jsonPath = "get_post.json", responseTime = 1000)
Call<Post> getPost(@Path("id") int postId);
```
That's it. After enabling OffIt, your call will be mocked with data passed to the annotation. And it will behave like the real one!

#### Second way: Advanced Mocking with custom Call class ####

Advanced, more powerful, way to mock things up is by using modified ```Call``` and ```CallBack``` classes. 
Together with ```@Mockables``` annotation, it gives you the ability to use tags so you can mock many case scenarios.

```java
@GET("/posts/{id}")
@Mockables({
    @Mockable(tag = "success", responseCode = 200, jsonPath = "get_post.json", responseTime = 3500),
    @Mockable(tag = "no_post", responseCode = 422, jsonPath = "get_post_error.json", responseTime = 4500)})
com.gabrielsamojlo.offit.Call<Post> getPost(@Path("id") int postId);
```

After that, in your Java code you can execute one of your tagged mockable:

```java
com.gabrielsamojlo.offit.Call<List<Post>> call = mApiService.getPost().withTag("no_post");
call.enqueue(new com.gabrielsamojlo.offit.Callback<List<Post>>() {
    @Override
    public void onResponse(com.gabrielsamojlo.offit.Call<List<Post>> call, Response<List<Post>> response) {
        // Your code
    }

    @Override
    public void onFailure(com.gabrielsamojlo.offit.Call<List<Post>> call, Throwable t) {
        // Your code
    }
});
```

In addition, using this method you can easily configure parameters of your mocked call from Java code :

```java
com.gabrielsamojlo.offit.Call<List<Post>> call = mApiService.getPosts()
                .withResponseTime(100)
                .withResponseCode(404);
```

Of course, you can get rid of those ```com.gabrielsamojlo.offit``` prefixes by removing imports to original Retrofit classes.

#### Third way: Advanced Mocking with casting

This approach is a combination of two previous ones. You will have to annotate your API Service but this time, you can leave *original retrofit Call* as a return type:

```java
@GET("/posts/{id}")
@Mockables({
    @Mockable(tag = "success", responseCode = 200, jsonPath = "get_post.json", responseTime = 3500),
    @Mockable(tag = "no_post", responseCode = 422, jsonPath = "get_post_error.json", responseTime = 4500)})
Call<Post> getPost(@Path("id") int postId);
```

Now, when you are about to enqueue your call (and you want to use some of OffIt custom methods) you have to cast original Retrofit Call to MockableCall. Just like this:

```java
Call<List<Post>> call = mApiService.getPosts();
((MockableCall<List<Post>>) call).withResponseCode(200).withResponseTime(1000).enqueue(new Callback<List<Post>>() {
    @Override
    public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
        // Your stuff here
    }

    @Override
    public void onFailure(@NonNull retrofit2.Call<List<Post>> call, @NonNull Throwable t) {
        // Your stuff here
    }
});
```

### OffIt in tests

You can easily use OffIt to mock your API Responses in your tests. If you want to test everything synchronously there is nothing you need to do more than explained in previous steps. 
You just simply invoke a `.execute()` method on your calls. If you want to test your asynchronous callbacks you will need to add `OffitTestRule` to your tests just like this:

```java
@Rule
public TestRule offitRule = new OffitTestRule();
```

And... that's it. Now all your `.enqueue()` methods invoked on calls will be executed on the main thread for you so don't worry about asynchronous testing!

For more detailed example, please see `OffItIntegrationTests` under `app/src/androidTest/..` in source.


### Roadmap

Please keep in mind that OffIt is still under development. There are some tweaks on the roadmap, so be ready for fixes, tweaks and new features.
Some of the planned features are listed here with progress on them. Feel free to suggest improvements!

- [ ] Network simulator
- [ ] Headers support

### Support, contact and contribution

Feel free to contact me at gabrielsamojlo@gmail.com for any questions.
Any forms of contribution are welcome, so feel free to fork and contribute with pull requests.

### License
```
Copyright 2018 Gabriel Samojło

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
