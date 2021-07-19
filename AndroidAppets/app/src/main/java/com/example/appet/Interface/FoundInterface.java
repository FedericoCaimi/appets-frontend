package com.example.appet.Interface;

import com.example.appet.Model.FoundModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FoundInterface {
    @POST("posts")
    Call<String> registerSeenPet(@Body FoundModel body);
}
