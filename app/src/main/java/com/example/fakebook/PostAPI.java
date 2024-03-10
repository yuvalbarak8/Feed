package com.example.fakebook;

// File: PostAPI.java

import com.example.fakebook.model.Post;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostAPI {

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public PostAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get() {
        Call<List<Post>> call = webServiceAPI.getAllPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> posts = response.body();
                    // Logic to handle the retrieved posts
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Handle the failure
            }
        });
    }

    // Other methods such as createPost or deletePost might also be present here.
}
