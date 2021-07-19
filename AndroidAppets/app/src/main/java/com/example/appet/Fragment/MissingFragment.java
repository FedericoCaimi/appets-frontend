package com.example.appet.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.appet.Adapter.MissingPetAdapter;
import com.example.appet.Adapter.PetAdapter;
import com.example.appet.Interface.PetInterface;
import com.example.appet.LoadingDialog;
import com.example.appet.Model.Pet;
import com.example.appet.Model.Post;
import com.example.appet.Model.Tag;
import com.example.appet.R;
import com.example.appet.Services.PetService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissingFragment extends Fragment {

    RecyclerView recyclerViewPets;
    RecyclerView recyclerViewMissingPets;
    ArrayList<Pet> pets;
    ArrayList<Pet> missingPets;
    PetInterface petInterface;
    PetAdapter petadapter;
    MissingPetAdapter missingPetadapter;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager missingLinearLayoutManager;
    ImageView addPet;
    LoadingDialog loadingDialog;
    private ViewGroup layout;
    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_missing, container, false);
        // Inflate the layout for this fragment
        pets = new ArrayList<>();
        missingPets = new ArrayList<>();
        recyclerViewPets = view.findViewById(R.id.recyclerPets);
        recyclerViewMissingPets = view.findViewById(R.id.recyclerMissingPets);
        addPet = view.findViewById(R.id.addPet);

        loadingDialog = new LoadingDialog(getActivity());

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewPets.setLayoutManager(linearLayoutManager);

        missingLinearLayoutManager = new LinearLayoutManager(getContext());
        missingLinearLayoutManager.setReverseLayout(true);
        missingLinearLayoutManager.setStackFromEnd(true);
        recyclerViewMissingPets.setLayoutManager(missingLinearLayoutManager);

        petInterface = new PetService();
        String userId = getUserId();
        token = getToken();

        getPets(userId);

        petadapter = new PetAdapter(pets);
        recyclerViewPets.setAdapter(petadapter);

        missingPetadapter = new MissingPetAdapter(missingPets);
        recyclerViewMissingPets.setAdapter(missingPetadapter);

        petadapter.setOnClickListener(new PetAdapter.OnItemClickListener() {
            @Override
            public void onMissingClick(int position) {
                Pet pet = pets.get(position);
                String name = pet.getName();
                String petId = pet.getId();
                String phone = pet.getPhone();
                //String description = pet.getType();
                String image = pet.getImage();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FoundFragment foundFragment = new FoundFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragmentHeader", "Reportar mascota perdida");
                bundle.putString("image", image);
                bundle.putString("title", "Se perdió " + name+"!");
                bundle.putString("description", "Se perdió un ser muy importante para nosotros, ayúdanos a encontralo! Responde al nombre de "+name+".");
                bundle.putString("petId",petId);
                bundle.putString("type","0");
                bundle.putString("phone",phone);

                List<Tag> tagList = pet.getTags();
                String animal = "";
                String breed = "";
                int red = 0;
                int green = 0;
                int blue = 0;
                for(Tag tag : tagList){
                    switch (tag.getType()){
                        case "Animal":
                            animal = tag.getValue();
                            break;
                        case "Breed":
                            breed = tag.getValue();
                            break;
                        case "RedColor":
                            red = Integer.parseInt(tag.getValue());
                            break;
                        case "GreenColor":
                            green = Integer.parseInt(tag.getValue());
                            break;
                        case "BlueColor":
                            blue = Integer.parseInt(tag.getValue());
                            break;
                    }
                }
                int RGB = android.graphics.Color.rgb(red, green, blue);
                bundle.putString("Animal",animal);
                bundle.putString("Breed",breed);
                bundle.putString("Color",String.valueOf(RGB));

                foundFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                        .replace(R.id.fragment_container,foundFragment,"FOUND_FRAGMENT")
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }

            @Override
            public void onEditClick(int position) {
                Pet pet = pets.get(position);
                String name = pet.getName();
                String petId = pet.getId();
                String type = pet.getType();
                String image = pet.getImage();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddPetFragment addFragment = new AddPetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragmentHeader", "Modificar mascota");
                bundle.putString("image", image);
                bundle.putString("name", name);
                bundle.putString("type", type);
                bundle.putString("petId",petId);
                bundle.putString("fragmentType", "modifyPet");

                List<Tag> tagList = pet.getTags();
                String animal = "";
                String breed = "";
                int red = 0;
                int green = 0;
                int blue = 0;
                for(Tag tag : tagList){
                    switch (tag.getType()){
                        case "Animal":
                            animal = tag.getValue();
                            break;
                        case "Breed":
                            breed = tag.getValue();
                            break;
                        case "RedColor":
                            red = Integer.parseInt(tag.getValue());
                            break;
                        case "GreenColor":
                            green = Integer.parseInt(tag.getValue());
                            break;
                        case "BlueColor":
                            blue = Integer.parseInt(tag.getValue());
                            break;
                    }
                }
                int RGB = android.graphics.Color.rgb(red, green, blue);
                bundle.putString("Animal",animal);
                bundle.putString("Breed",breed);
                bundle.putString("Color",String.valueOf(RGB));

                addFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                        .replace(R.id.fragment_container,addFragment,"ADD_PET_FRAGMENT")
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder warning = new AlertDialog.Builder(view.getContext());
                warning.setTitle("Atención");
                warning.setMessage("¿Estas seguro que quieres eliminar esta mascota?");
                warning.setCancelable(false);
                warning.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        String petId = pets.get(position).getId();
                        Call<String> deletePet = petInterface.deletePet(token, petId);

                        deletePet.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(!response.isSuccessful()){
                                    //veremos manejo de error se obtiene el codigo asi:
                                    //response.code();
                                }
                                final String petId = response.body();
                                boolean stop = false;
                                for (int i = 0; i < pets.size() && !stop; ++i) {
                                    if(pets.get(i).getId().equals(petId)) {
                                        pets.remove(i);
                                        stop = true;
                                    }
                                }
                                petadapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                            }
                        });
                    }
                });
                warning.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) { }
                });
                warning.show();
            }
        });

        missingPetadapter.setOnClickListener(new MissingPetAdapter.OnItemClickListener() {
            final View G = view;
            @Override
            public void onMissingClick(final int position) {
                AlertDialog.Builder warning = new AlertDialog.Builder(view.getContext());
                warning.setTitle("Atención");
                warning.setMessage("¿Estas seguro que quieres marcar esta mascota como encontrada?");
                warning.setCancelable(false);
                warning.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        String petId = missingPets.get(position).getId();
                        Call<Pet> findPets = petInterface.findPet(token, petId);

                        findPets.enqueue(new Callback<Pet>() {
                            @Override
                            public void onResponse(Call<Pet> call, Response<Pet> response) {
                                if(!response.isSuccessful()){
                                    //veremos manejo de error se obtiene el codigo asi:
                                    //response.code();
                                }
                                final Pet petRes = response.body();
                                pets.add(petRes);
                                boolean stop = false;
                                for (int i = 0; i < missingPets.size() && !stop; ++i) {
                                    if(missingPets.get(i).getId().equals(petRes.getId())) {
                                        missingPets.remove(i);
                                        stop = true;
                                    }
                                }
                                petadapter.notifyDataSetChanged();
                                missingPetadapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<Pet> call, Throwable t) {
                            }
                        });
                    }
                });
                warning.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) { }
                });
                warning.show();
            }

            @Override
            public void onFoundClick(int position) {
                List<Post> petPosts = new ArrayList<>();
                petPosts = missingPets.get(position).getPosts();
                int len = petPosts.size();
                Post post =petPosts.get(len-1);
                String postId = post.getPostId();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                MatchFragment matchFragment = new MatchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                bundle.putString("petId", missingPets.get(position).getId());
                matchFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                        .replace(R.id.fragment_container,matchFragment,"MATCH_FRAGMENT")
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

                /**
                 * int len = pet.getPosts().size();
                 *                     if(pet.getPosts() != null && len> 0 && (pet.getPosts().get(len-1).getType()) == 0) {
                 */

            }

            @Override
            public void onEditClick(int position) {
                List<Post> petPosts = new ArrayList<>();
                petPosts = missingPets.get(position).getPosts();
                int len = petPosts.size();
                Post post =petPosts.get(len-1);
                String postId = post.getPostId();
                String petPhone = post.getReporterPhone();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FoundFragment foundFragment = new FoundFragment();
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                bundle.putString("fragmentHeader", "Reportar mascota perdida");
                bundle.putString("image", post.getPostImage());
                bundle.putString("title", post.getPostTitle());
                bundle.putString("phone", petPhone);
                bundle.putString("description", post.getDescription());
                bundle.putString("petId",missingPets.get(position).getId());
                bundle.putString("mode","edit");
                bundle.putString("type","0");
                List<Tag> tagList = post.getTags();
                String animal = "";
                String breed = "";
                int red = 0;
                int green = 0;
                int blue = 0;
                for(Tag tag : tagList){
                    switch (tag.getType()){
                        case "Animal":
                            animal = tag.getValue();
                            break;
                        case "Breed":
                            breed = tag.getValue();
                            break;
                        case "RedColor":
                            red = Integer.parseInt(tag.getValue());
                            break;
                        case "GreenColor":
                            green = Integer.parseInt(tag.getValue());
                            break;
                        case "BlueColor":
                            blue = Integer.parseInt(tag.getValue());
                            break;
                    }
                }
                int RGB = android.graphics.Color.rgb(red, green, blue);
                bundle.putString("Animal",animal);
                bundle.putString("Breed",breed);
                bundle.putString("Color",String.valueOf(RGB));
                foundFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                        .replace(R.id.fragment_container,foundFragment,"FOUND_FRAGMENT")
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });

        addPet.setOnClickListener(new View.OnClickListener()
        { public void onClick(View v)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            AddPetFragment addPetFragment = new AddPetFragment();

            Bundle bundle = new Bundle();
            bundle.putString("fragmentType", "addPet");
            addPetFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                    .replace(R.id.fragment_container,addPetFragment,"ADD_PET_FRAGMENT")
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
        });

        return view;

    }

    private void getPets(String userId){

        loadingDialog.startLoading();
        Call<ArrayList<Pet>> callPets = petInterface.getPetsByOwner(token, userId);

        callPets.enqueue(new Callback<ArrayList<Pet>>() {
            @Override
            public void onResponse(Call<ArrayList<Pet>> call, Response<ArrayList<Pet>> response) {
                if(!response.isSuccessful()){
                    //veremos manejo de error se obtiene el codigo asi:
                    //response.code();
                }
                loadingDialog.dismissLoading();
                ArrayList<Pet> petsRes = response.body();
                if(petsRes != null){
                    for(Pet pet:petsRes){
                        int len = pet.getPosts().size();
                        if(pet.getPosts() != null && len> 0 && (pet.getPosts().get(len-1).getType()) == 0) {//pet.getPosts().get(0).getType()
                            missingPets.add(new Pet(pet.getId(), pet.getName(),pet.getType(), pet.getImage(), pet.isDeleted(), pet.getTags(), pet.getOwnerId(), pet.getPosts(), pet.getPhone()));
                        } else {
                            pets.add(new Pet(pet.getId(), pet.getName(), pet.getType(), pet.getImage(), pet.isDeleted(), pet.getTags(), pet.getOwnerId(), pet.getPosts(), pet.getPhone()));
                        }
                    }
                }
                petadapter.notifyDataSetChanged();
                missingPetadapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<Pet>> call, Throwable t) {
                loadingDialog.dismissLoading();
            }
        });

    }

    private String getUserId(){
        String files [] = getActivity().fileList();
        String userId = "";
        if(ExistFile(files,"user.txt")){
            try {
                InputStreamReader file = new InputStreamReader(getActivity().openFileInput("user.txt"));
                BufferedReader bufferedReader = new BufferedReader(file);
                String line = bufferedReader.readLine().split("/")[0];

                if(line != null){
                    userId = line.trim();
                }
                bufferedReader.close();
                file.close();
            }catch (IOException e){

            }
        }
        return userId;
    }

    private String getToken(){
        String files [] = getActivity().fileList();
        String token = "";
        if(ExistFile(files,"token.txt")){
            try {
                InputStreamReader file = new InputStreamReader(getActivity().openFileInput("token.txt"));
                BufferedReader bufferedReader = new BufferedReader(file);
                String line = bufferedReader.readLine();

                if(line != null){
                    token = line.trim();
                }
                bufferedReader.close();
                file.close();
            }catch (IOException e){

            }
        }
        return token;
    }

    private boolean ExistFile(String files [], String fileName){
        for (int i = 0; i < files.length; i++){
            if(fileName.equals(files[i])){
                return true;
            }
        }
        return false;
    }
}