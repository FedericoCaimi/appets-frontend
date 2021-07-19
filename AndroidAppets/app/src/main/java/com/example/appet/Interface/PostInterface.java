package com.example.appet.Interface;

import com.example.appet.Model.FoundModel;
import com.example.appet.Model.Post;
import com.example.appet.Model.SimilarPost;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostInterface {

    @GET("posts")
    Call<ArrayList<Post>> getPosts(@Query("range") int range, @Query("ubication") String ubication);

    @POST("posts/{petId}")
    Call<String> missingPet(@Header("auth") String token, @Body FoundModel body, @Path("petId") String userId);

    @GET("posts/taganimals")
    Call<String[]> getAnimals();

    @GET("posts/tagBreeds?")
    Call<String []> getBreeds(@Query("animal") String animal);

    @GET("posts/{postId}/similarPosts")
    Call<ArrayList<SimilarPost>> similarPosts(@Path("postId") String postId);

    @PUT("posts/{postId}/ignoreSimilar/{IdIgnoredPost}")
    Call<Post> ignoreSimilar(@Path("postId") String postId, @Path("IdIgnoredPost") String IdIgnoredPost);

    @PUT("posts/{postId}")
    Call<Post> editPost(@Path("postId") String postId, @Body FoundModel body);
}
