package com.example.appet.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appet.Adapter.PostAdapter;
import com.example.appet.Interface.PostInterface;
import com.example.appet.LoadingDialog;
import com.example.appet.MainActivity;
import com.example.appet.Model.Post;
import com.example.appet.R;
import com.example.appet.Services.PostService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int PERMISSION_FINE_LOCATION = 99;
    RecyclerView recyclerViewPosts;
    ArrayList<Post> posts;
    PostInterface callback;
    PostAdapter adapter;
    ImageView settings;
    LinearLayoutManager linearLayoutManager;
    PopupMenu popupMenu;
    Boolean isLoggedIn;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String coordinates;
    int eventsRange;
    LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(65000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoading();
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                String ubication = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
                coordinates = ubication;

                callback = new PostService();
                getPosts();

                adapter = new PostAdapter(posts);
                adapter.setOnClickListener(new PostAdapter.OnItemClickListener() {
                    @Override
                    public void onMapClick(int position) {
                        String ubication = posts.get(position).getUbication();

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Intent chooser = Intent.createChooser(intent, "Abrir mapa");

                        String uri = "geo:0,0?q="+ ubication + " (name)";

                        intent.setData(Uri.parse(uri));
                        startActivity(chooser);
                    }

                });
                recyclerViewPosts.setAdapter(adapter);

            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        startLocationUpdates();

        updateGPS();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_home, container, false);
        posts = new ArrayList<>();
        isLoggedIn = getIsLoggedInStatus();
        recyclerViewPosts = vista.findViewById(R.id.recyclerPostsId);
        settings = vista.findViewById(R.id.settings);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                showPopup(v);
            }
        });

        String files [] = getActivity().fileList();
        if(ExistFile(files,"config.txt")){
            try {
                InputStreamReader file = new InputStreamReader(getActivity().openFileInput("config.txt"));
                BufferedReader bufferedReader = new BufferedReader(file);
                String line = bufferedReader.readLine();
                String range = "";
                if(line != null){
                    range = line.trim();
                }else{
                    eventsRange = 1500;
                }
                bufferedReader.close();
                file.close();
                eventsRange = Integer.parseInt(range);
            }catch (IOException e){

            }
        }
        return vista;
    }


    public void showPopup(View v) {
        popupMenu = new PopupMenu(getActivity(), v);
        handlePopupMenuClickListener();
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.editUser).setVisible(isLoggedIn);
        popupMenu.show();
    }

    private void handlePopupMenuClickListener(){
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                switch (item.getItemId()) {
                    case R.id.editRange:
                        SettingsFragment settingsFragment = new SettingsFragment();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                .replace(R.id.fragment_container,settingsFragment,"SETTINGS_FRAGMENT")
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        return true;
                    case R.id.editUser:
                        RegistrationFragment registrationFragment = new RegistrationFragment();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                .replace(R.id.fragment_container,registrationFragment,"REGISTRATION_FRAGMENT")
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getPosts(){
        Call<ArrayList<Post>> callPost = callback.getPosts(eventsRange,"" + coordinates);

        callPost.enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                posts.clear();
                if(!response.isSuccessful()){
                    //veremos manejo de error se obtiene el codigo asi:
                    //response.code();
                }
                ArrayList<Post> postsRes = response.body();
                if(postsRes != null){
                    for(Post post:postsRes){
                        posts.add(new Post(post.getPostId(), post.getPostTitle(),post.getPostImage(), post.getDescription(), post.getUbication(), post.getType(), post.getTags(), post.getReporterPhone()));
                    }
                    adapter.notifyDataSetChanged();
                }
                loadingDialog.dismissLoading();
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
            }
        });
    }

    private boolean getIsLoggedInStatus(){
        Boolean isLoggedIn = false;
        String files [] = getActivity().fileList();
        if(ExistFile(files,"token.txt") && ExistFile(files,"user.txt")){
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    private void updateGPS() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String ubication = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
                    coordinates = ubication;

                    callback = new PostService();
                    getPosts();
                    adapter = new PostAdapter(posts);
                    recyclerViewPosts.setAdapter(adapter);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 99 && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            updateGPS();
        }else if(requestCode == 99){
            Toast.makeText(getActivity(),"Permiso denegado",Toast.LENGTH_SHORT).show();
        }
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