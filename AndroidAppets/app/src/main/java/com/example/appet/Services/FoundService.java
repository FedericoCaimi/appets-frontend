package com.example.appet.Services;

import com.example.appet.BuildConfig;
import com.example.appet.Interface.FoundInterface;
import com.example.appet.Model.FoundModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoundService implements FoundInterface {
    @Override
    public Call<String> registerSeenPet(FoundModel body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoundInterface foundInterface = retrofit.create(FoundInterface.class);
        return foundInterface.registerSeenPet(body);
    }
}
