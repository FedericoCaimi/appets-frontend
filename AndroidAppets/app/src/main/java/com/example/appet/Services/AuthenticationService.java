package com.example.appet.Services;

import com.example.appet.BuildConfig;
import com.example.appet.Interface.AuthenticationInterface;
import com.example.appet.Model.LoginModel;
import com.example.appet.Model.Session;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationService implements AuthenticationInterface {

    @Override
    public Call<Session> login(LoginModel body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthenticationInterface authenticationInterface = retrofit.create(AuthenticationInterface.class);
        return authenticationInterface.login(body);
    }
}
