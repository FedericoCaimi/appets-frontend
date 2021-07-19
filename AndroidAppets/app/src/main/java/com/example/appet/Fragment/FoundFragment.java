package com.example.appet.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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

import com.example.appet.Interface.FoundInterface;
import com.example.appet.Interface.PostInterface;
import com.example.appet.Model.FoundModel;
import com.example.appet.Model.Post;
import com.example.appet.Model.Tag;
import com.example.appet.R;
import com.example.appet.Services.FoundService;
import com.example.appet.Services.PostService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import retrofit2.http.POST;
import yuku.ambilwarna.AmbilWarnaDialog;

public class FoundFragment extends Fragment {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int CAPTURE_IMAGE_REQUEST = 1;
    EditText descriptionInput, titleInput, ubicationInput, phoneNumber;
    Button foundButton, color;//, changeImageButton;
    ImageView foundImage, changeImageButton;
    FoundInterface foundService;
    PostInterface postService;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextInputLayout locationLayout;
    LocationRequest locationRequest;
    TextView fragmentHeader;
    Spinner animals, breeds;
    int defaultColor, RColor, GColor, BColor;
    String inputAnimal, inputBreed, inputColor, phone;

    ArrayAdapter<String> animalAdapter, breedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        foundService = new FoundService();
        postService = new PostService();

        View fragment = inflater.inflate(R.layout.fragment_found, container, false);

        locationLayout = fragment.findViewById(R.id.locationLayout);
        fragmentHeader = fragment.findViewById(R.id.textView);
        descriptionInput = fragment.findViewById(R.id.description);
        titleInput = fragment.findViewById(R.id.title);
        ubicationInput = fragment.findViewById(R.id.location);
        phoneNumber = fragment.findViewById(R.id.phoneNumber);
        foundButton = fragment.findViewById(R.id.confirmButton);
        changeImageButton = fragment.findViewById(R.id.changeImageButton);
        foundImage = fragment.findViewById(R.id.foundImage);
        color = fragment.findViewById(R.id.color);

        animals = fragment.findViewById(R.id.animalsTag);
        breeds = fragment.findViewById(R.id.raceTag);

        locationLayout.setVisibility(View.GONE);
        getAnimals();

        String newFragmentHeader = this.getArguments().getString("fragmentHeader");
        final String postId = this.getArguments().getString("postId");
        String newTitle = this.getArguments().getString("title");
        String newDescription = this.getArguments().getString("description");
        String stringType = this.getArguments().getString("type");
        final String petId = this.getArguments().getString("petId");
        final int type = Integer.parseInt(stringType);
        String stringImage = this.getArguments().getString("image");
        final String mode = this.getArguments().getString("mode");
        inputAnimal = this.getArguments().getString("Animal");
        inputBreed = this.getArguments().getString("Breed");
        inputColor = this.getArguments().getString("Color");
        phone = this.getArguments().getString("phone");

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

        Bitmap bitmapImage = StringToBitMap(stringImage);

        fragmentHeader.setText(newFragmentHeader);
        titleInput.setText(newTitle);
        descriptionInput.setText(newDescription);
        foundImage.setImageBitmap(bitmapImage);
        phoneNumber.setText(phone);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocation();

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

        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View G = v;
                String description = descriptionInput.getText().toString();
                String title = titleInput.getText().toString();
                String ubication = ubicationInput.getText().toString();
                String image = BitMapToString(((BitmapDrawable)foundImage.getDrawable()).getBitmap());
                String phone = phoneNumber.getText().toString();
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

                FoundModel body = new FoundModel(description, title, ubication, image, type,tagList, phone);
                if(type == 1){

                    Call<String> foundResponse = foundService.registerSeenPet(body);
                    foundResponse.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            new AlertDialog.Builder(G.getContext())
                                    .setTitle("Mascota vista")
                                    .setMessage("Su post ha sido creado con éxito")
                                    .show();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            HomeFragment homeFragment = new HomeFragment();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                    .replace(R.id.fragment_container,homeFragment,"HOME_FRAGMENT")
                                    .addToBackStack(null)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            new AlertDialog.Builder(G.getContext())
                                    .setTitle("Error")
                                    .setMessage("No se ha registrado su post correctamente")
                                    .show();
                        }
                    });
                }else{
                    if(mode == "edit"){

                        Call<Post> editPostResponse = postService.editPost(postId, body);
                        editPostResponse.enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(Call<Post> call, Response<Post> response) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Exito")
                                        .setMessage("se ha actualizado su post correctamente")
                                        .show();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                HomeFragment homeFragment = new HomeFragment();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_rigth_to_left, R.anim.exit_rigth_to_left, R.anim.enter_left_to_rigth, R.anim.exit_left_to_rigth)
                                        .replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT")
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                            }

                            @Override
                            public void onFailure(Call<Post> call, Throwable t) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Error")
                                        .setMessage("No se ha actualizado su post correctamente")
                                        .show();
                            }
                        });
                    }else {
                        String token = getToken();
                        Call<String> foundResponse = postService.missingPet(token, body, petId);
                        foundResponse.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Mascota perdida!")
                                        .setMessage("Ojala la encuentre pronto...")
                                        .show();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                HomeFragment homeFragment = new HomeFragment();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_rigth_to_left, R.anim.exit_rigth_to_left, R.anim.enter_left_to_rigth, R.anim.exit_left_to_rigth)
                                        .replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT")
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Error")
                                        .setMessage("No se ha registrado su post correctamente")
                                        .show();
                            }
                        });
                    }
                }

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
        // Inflate the layout for this fragment

        return fragment;
    }

    private void getLocation(){
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location != null){
                            String ubication = String.valueOf(location.getLatitude())+", "+String.valueOf(location.getLongitude());
                            ubicationInput.setText(ubication);
                        }
                        else {
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000).setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback(){
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location lastLocation = locationResult.getLastLocation();
                                    String ubication = String.valueOf(lastLocation.getLatitude())+", "+String.valueOf(lastLocation.getLongitude());
                                    ubicationInput.setText(ubication);
                                }
                            };
                            //fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                        }
                    }
                });
            }
            //else
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }
        }
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
            foundImage.setImageBitmap(image);
        } else {
            Toast.makeText(getActivity(),"Algo salió mal!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            if(requestCode == 100){
                getLocation();
            }else if (requestCode == 0){
                captureImage();
            }
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
}