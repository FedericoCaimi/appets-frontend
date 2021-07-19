package com.example.appet.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appet.Interface.PetInterface;
import com.example.appet.Interface.PostInterface;
import com.example.appet.Interface.UserServiceInterface;
import com.example.appet.Model.Pet;
import com.example.appet.Model.PetOut;
import com.example.appet.Model.RegistrationModel;
import com.example.appet.Model.Tag;
import com.example.appet.R;
import com.example.appet.Services.PetService;
import com.example.appet.Services.PostService;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;


public class AddPetFragment extends Fragment {

    private static final int CAPTURE_IMAGE_REQUEST = 1;
    EditText name;
    //EditText type;
    Button confirmButton;
    PetInterface petInterface;
    PostInterface postService;
    ImageView imageView, changeImageButton;
    TextView fragmentHeader;
    //TextInputLayout typeLayout;
    Button color;
    Spinner animals, breeds;
    int defaultColor, RColor, GColor, BColor;
    String inputAnimal, inputBreed, inputColor, token;

    ArrayAdapter<String> animalAdapter, breedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        name = view.findViewById(R.id.name);

        imageView = view.findViewById(R.id.imageView);
        changeImageButton = view.findViewById(R.id.changeImageButton);
        confirmButton = view.findViewById(R.id.confirmButton);
        fragmentHeader = view.findViewById(R.id.textView);
        petInterface = new PetService();
        postService = new PostService();
        color = view.findViewById(R.id.color);

        animals = view.findViewById(R.id.animalsTag);
        breeds = view.findViewById(R.id.raceTag);

        getAnimals();

        token = getToken();
        inputAnimal = this.getArguments().getString("Animal");
        inputBreed = this.getArguments().getString("Breed");
        inputColor = this.getArguments().getString("Color");
        if(inputColor != null){
            color.setBackgroundColor(Integer.parseInt(inputColor));
            defaultColor = Integer.parseInt(inputColor);
            RColor = (defaultColor >> 16) & 0xff;
            GColor = (defaultColor >>  8) & 0xff;
            BColor = (defaultColor      ) & 0xff;
        }else{
            RColor = 0;
            GColor = 0;
            BColor = 0;
            int RGB = android.graphics.Color.rgb(RColor, GColor, RColor);
            defaultColor = RGB;

        }
        color.setBackgroundColor(defaultColor);

        final String fragmentType = this.getArguments().getString("fragmentType");
        final String petId = this.getArguments().getString("petId");

        if (fragmentType == "modifyPet"){
            String fragmentHeaderArg = this.getArguments().getString("fragmentHeader");
            String nameArg = this.getArguments().getString("name");
            String typeArg = this.getArguments().getString("type");
            String stringImage = this.getArguments().getString("image");
            Bitmap bitmapImage = StringToBitMap(stringImage);

            fragmentHeader.setText(fragmentHeaderArg);
            name.setText(nameArg);
            //type.setText(typeArg);
            imageView.setImageBitmap(bitmapImage);
        }

        animals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedAnimal = animalAdapter.getItem(position);
                getBreeds(selectedAnimal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View G = v;
                String petName = name.getText().toString();
               // String petType = type.getText().toString();
                String image = BitMapToString(((BitmapDrawable)imageView.getDrawable()).getBitmap());
                List<Tag> tagList = new ArrayList<>();
                Tag animalTag = new Tag("Animal",animals.getSelectedItem().toString());
                tagList.add(animalTag);
                Tag breedTag = new Tag("Breed",breeds.getSelectedItem().toString());
                tagList.add(breedTag);
                Tag RedTag = new Tag("RedColor",String.valueOf(RColor));
                tagList.add(RedTag);
                Tag GreenTag = new Tag("GreenColor",String.valueOf(GColor));
                tagList.add(GreenTag);
                Tag BlueTag = new Tag("BlueColor",String.valueOf(BColor));
                tagList.add(BlueTag);
                PetOut body = new PetOut(petName, "", image,false, tagList);
                String userId = getUserId();//"FD965E0F-CCBA-4973-4EFF-08D8844F968B";

                if(fragmentType.equals("addPet")) {
                    addPet(G, body, userId);
                } else if (fragmentType.equals("modifyPet")){
                    modifyPet(G, body);
                }
              }

            private void modifyPet(final View g, PetOut body) {
                Call<Pet> petResponse = petInterface.modifyPet(token, petId, body);
                petResponse.enqueue(new Callback<Pet>() {
                    @Override
                    public void onResponse(Call<Pet> call, Response<Pet> response) {
                        Pet petRes = response.body();

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        MissingFragment missingFragment = new MissingFragment();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                .replace(R.id.fragment_container,missingFragment,"MISSING_FRAGMENT")
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

                        new AlertDialog.Builder(g.getContext())
                                .setTitle("Exito!")
                                .setMessage("Mascota modificada")
                                .show();
                    }

                    @Override
                    public void onFailure(Call<Pet> call, Throwable t) {
                        new AlertDialog.Builder(g.getContext())
                                .setTitle("Algo salio mal!")
                                .setMessage("Error de comunicacion con el servidor")
                                .show();
                    }

                });
            }

            private void addPet(final View g, PetOut body, String userId) {
                Call<String> petResponse = petInterface.addPet(token, userId, body);
                petResponse.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String url = response.body();

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        MissingFragment missingFragment = new MissingFragment();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                .replace(R.id.fragment_container,missingFragment,"MISSING_FRAGMENT")
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

                        new AlertDialog.Builder(g.getContext())
                                .setTitle("Exito!")
                                .setMessage("Mascota registrada")
                                .show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        new AlertDialog.Builder(g.getContext())
                                .setTitle("Algo salio mal!")
                                .setMessage("Error de comunicacion con el servidor")
                                .show();
                    }

                });
            }
        });

        changeImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View G = v;
                Context mainActivity = getActivity();

                if (ContextCompat.checkSelfPermission(mainActivity, "android.permission.CAMERA") != 0)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},0);
                }
                else
                {
                    captureImage();
                }
            }
        });

        return view;
    }

    private void captureImage(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } catch (Exception exception) {
                // Error occurred while creating the File
                Toast.makeText(getActivity(),"Capture Image Bug: " + exception.toString(),Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getActivity(),"Cannot capture image at this time.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == this.CAPTURE_IMAGE_REQUEST && resultCode == -1) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);
        } else {
            Toast.makeText(getActivity(),"Algo salió mal!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0 && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            captureImage();
        }else{
            Toast.makeText(getActivity(),"Permiso denegado",Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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


    private void openColorPicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int colorFromPicker) {
                defaultColor = colorFromPicker;
                color.setBackgroundColor(defaultColor);
                //A = (defaultColor >> 24) & 0xff; // or color >>> 24
                RColor = (defaultColor >> 16) & 0xff;
                GColor = (defaultColor >>  8) & 0xff;
                BColor = (defaultColor      ) & 0xff;
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        dialog.show();
    }

    public void getAnimals(){
        Call<String []> getResponse = postService.getAnimals();
        getResponse.enqueue(new Callback<String []>() {
            @Override
            public void onResponse(Call<String []> call, Response<String []> response) {
                String [] animalList = response.body();
                animalAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,animalList);
                animals.setAdapter(animalAdapter);
                int pos = getPositionValue(animals, inputAnimal);
                animals.setSelection(pos);
            }
            @Override
            public void onFailure(Call<String []> call, Throwable t) {

            }
        });
    }

    private int getPositionValue(Spinner spinner, String input) {
        int position = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(input) ){
                position = i;
            }
        }
        return position;
    }

    public void getBreeds(String animal){
        Call<String []> getResponse = postService.getBreeds(animal);
        getResponse.enqueue(new Callback<String []>() {
            @Override
            public void onResponse(Call<String []> call, Response<String []> response) {
                String [] breedList = response.body();
                breedAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,breedList);
                breeds.setAdapter(breedAdapter);
                int pos = getPositionValue(breeds, inputBreed);
                breeds.setSelection(pos);
            }
            @Override
            public void onFailure(Call<String []> call, Throwable t) {

            }
        });
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