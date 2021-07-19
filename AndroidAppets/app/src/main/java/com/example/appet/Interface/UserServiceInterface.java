package com.example.appet.Interface;

import com.example.appet.Model.LoginModel;
import com.example.appet.Model.RegistrationModel;
import com.example.appet.Model.Session;
import com.example.appet.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserServiceInterface {
    @POST("users")
    Call<String> registration(@Body RegistrationModel body);

    @PUT("users")
    Call<User> updateUser(@Header("auth") String token, @Query("id") String id, @Body RegistrationModel body);

    @GET("users/{userId}")
    Call<User> getUser(@Header("auth") String token, @Path("userId") String id);
}
