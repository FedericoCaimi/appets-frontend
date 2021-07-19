package com.example.appet.Services;

import com.example.appet.BuildConfig;
import com.example.appet.Interface.PetInterface;
import com.example.appet.Model.Pet;
import com.example.appet.Model.PetOut;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetService implements PetInterface {
    @Override
    public Call<ArrayList<Pet>> getPetsByOwner(String token, String userId) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PetInterface petInterface = retrofit.create(PetInterface.class);
        return petInterface.getPetsByOwner(token, userId);
    }

    @Override
    public Call<String> addPet(String token, String userId, PetOut body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PetInterface petInterface = retrofit.create(PetInterface.class);
        return petInterface.addPet(token, userId, body);
    }

    @Override
    public Call<Pet> findPet(String token, String petId) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PetInterface petInterface = retrofit.create(PetInterface.class);
        return petInterface.findPet(token, petId);
    }

    @Override
    public Call<Pet> modifyPet(String token, String petId, PetOut body) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PetInterface petInterface = retrofit.create(PetInterface.class);
        return petInterface.modifyPet(token, petId, body);
    }

    @Override
    public Call<String> deletePet(String token, String petId) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PetInterface petInterface = retrofit.create(PetInterface.class);
        return petInterface.deletePet(token, petId);
    }
}
