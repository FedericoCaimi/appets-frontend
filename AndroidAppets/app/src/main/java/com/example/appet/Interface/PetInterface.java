package com.example.appet.Interface;

import com.example.appet.Model.Pet;
import com.example.appet.Model.PetOut;
import com.example.appet.Model.RegistrationModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PetInterface {
    @GET("pets/owner/{userId}")
    Call<ArrayList<Pet>> getPetsByOwner(@Header("auth") String token, @Path("userId") String userId);

    @POST("pets/{userId}")
    Call<String> addPet(@Header("auth") String token, @Path("userId") String userId,@Body PetOut body);

    @PUT("pets/PetFound/{petId}")
    Call<Pet> findPet(@Header("auth") String token, @Path("petId") String petId);

    @PUT("pets/{petId}")
    Call<Pet> modifyPet(@Header("auth") String token, @Path("petId") String petId,@Body PetOut body);

    @DELETE("pets/{petId}")
    Call<String> deletePet(@Header("auth") String token, @Path("petId") String userId);
}
