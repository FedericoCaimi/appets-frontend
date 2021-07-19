package com.example.appet.Services;

import com.example.appet.BuildConfig;
import com.example.appet.Interface.PostInterface;
import com.example.appet.Model.FoundModel;
import com.example.appet.Model.Post;
import com.example.appet.Model.SimilarPost;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostService implements PostInterface {
    @Override
    public Call<ArrayList<Post>> getPosts(int range, String ubication) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.getPosts(range, ubication);
    }

    @Override
    public Call<String> missingPet(String token, FoundModel body, String petId) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.missingPet(token, body, petId);
    }

    @Override
    public Call<String []> getAnimals() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.getAnimals();
    }

    @Override
    public Call<String[]> getBreeds(String animal) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.getBreeds(animal);
    }

    @Override
    public Call<ArrayList<SimilarPost>> similarPosts(String postId) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.similarPosts(postId);
    }

    @Override
    public Call<Post> ignoreSimilar(String postId, String IdIgnoredPost) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.ignoreSimilar(postId, IdIgnoredPost);
    }

    @Override
    public Call<Post> editPost(String postId, FoundModel body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostInterface postInterface = retrofit.create(PostInterface.class);
        return postInterface.editPost(postId, body);
    }
}
