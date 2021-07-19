package com.example.appet.Interface;

import com.example.appet.Model.LoginModel;
import com.example.appet.Model.Post;
import com.example.appet.Model.Session;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationInterface {

    @POST("authentication")
    Call<Session> login(@Body LoginModel body);
}
