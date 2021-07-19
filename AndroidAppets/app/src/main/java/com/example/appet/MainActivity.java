package com.example.appet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.example.appet.Fragment.FoundFragment;
import com.example.appet.Fragment.HomeFragment;
import com.example.appet.Fragment.LoginFragment;
import com.example.appet.Fragment.MissingFragment;
import com.example.appet.Interface.LoginCommunicationInterface;
import com.example.appet.Interface.PostInterface;
import com.example.appet.Model.Post;
import com.example.appet.Services.PostService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LoginCommunicationInterface {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    Boolean isLoggedIn = false;
    String fragmentTag = "";
    Bundle foundBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //isLoggedIn = getIsLoggedInStatus();
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        FirebaseMessaging.getInstance().subscribeToTopic("lostPet")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d( "pushNotification", "push not FALLIDA");
                        }
                        Log.d( "pushNotification", "push not EXITOSA");
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            fragmentTag = "HOME_FRAGMENT";
                            break;
                        case R.id.nav_found:

                            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.CAMERA
                                },100);
                            }else {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(intent, 100);
                                }
                            }
                            break;
                        case R.id.nav_missing:
                            isLoggedIn = getIsLoggedInStatus();
                            selectedFragment = isLoggedIn
                                    ? new MissingFragment()
                                    : new LoginFragment();
                            fragmentTag = isLoggedIn
                                    ? "LOGIN_FRAGMENT"
                                    : "MISSING_FRAGMENT";
                            break;
                    }
                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment,fragmentTag).commit();
                    }
                    return true;
                }
            };

    @Override
    public void postLoginEvent() {
        isLoggedIn = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK) {
                selectedFragment = new FoundFragment();
                foundBundle.putString("fragmentHeader", "Reportar mascota vista");
                Bitmap image = (Bitmap) data.getExtras().get("data");
                String stringImage = BitMapToString(image);
                foundBundle.putString("image", stringImage);
                foundBundle.putString("type", "1");
                selectedFragment.setArguments(foundBundle);
                fragmentTag = "FOUND_FRAGMENT";

            }else{
                selectedFragment = new HomeFragment();
                fragmentTag = "HOME_FRAGMENT";
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment,fragmentTag).commit();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 100);
            }
        }else if(requestCode == 100){

            Toast.makeText(this,"Permiso denegado",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getIsLoggedInStatus(){
        Boolean isLoggedIn = false;
        String files [] = this.fileList();
        if(ExistFile(files,"token.txt") && ExistFile(files,"user.txt")){
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    private boolean ExistFile(String files [], String fileName){
        for (int i = 0; i < files.length; i++){
            if(fileName.equals(files[i])){
                return true;
            }
        }
        return false;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}