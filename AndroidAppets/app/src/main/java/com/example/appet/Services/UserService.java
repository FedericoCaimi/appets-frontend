package com.example.appet.Services;

import com.example.appet.BuildConfig;
import com.example.appet.Interface.UserServiceInterface;
import com.example.appet.Model.RegistrationModel;
import com.example.appet.Model.User;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService implements UserServiceInterface {
    @Override
    public Call<String> registration(RegistrationModel body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserServiceInterface userServiceInterface = retrofit.create(UserServiceInterface.class);
        return userServiceInterface.registration(body);
    }

    @Override
    public Call<User> updateUser(String token, String id, RegistrationModel body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserServiceInterface userServiceInterface = retrofit.create(UserServiceInterface.class);
        return userServiceInterface.updateUser(token, id, body);
    }

    @Override
    public Call<User> getUser(String token, String id) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserServiceInterface userServiceInterface = retrofit.create(UserServiceInterface.class);
        return userServiceInterface.getUser(token, id);
    }
}
