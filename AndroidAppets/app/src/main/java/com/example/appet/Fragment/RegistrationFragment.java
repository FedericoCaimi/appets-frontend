package com.example.appet.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.appet.Interface.UserServiceInterface;
import com.example.appet.Model.LoginModel;
import com.example.appet.Model.RegistrationModel;
import com.example.appet.Model.Session;
import com.example.appet.Model.User;
import com.example.appet.R;
import com.example.appet.Services.UserService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput, phoneInput;
    Button confirmButton;
    UserServiceInterface userServiceInterface;
    String userId;
    User user;
    String token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_registration, container, false);

        nameInput = fragment.findViewById(R.id.nameInput);
        emailInput = fragment.findViewById(R.id.emailInput);
        phoneInput = fragment.findViewById(R.id.phoneInput);
        passwordInput = fragment.findViewById(R.id.passwordInput);
        confirmPasswordInput = fragment.findViewById(R.id.confirmPasswordInput);
        confirmButton = fragment.findViewById(R.id.confirmButton);
        userServiceInterface = new UserService();

        getUserInfo();

        /*if(userId != null){
            nameInput.setText(user.getUserName());
            emailInput.setText(user.getEmail());
            emailInput.setEnabled(false);
        }*/

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View G = v;
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String phone = phoneInput.getText().toString();
                RegistrationModel body = new RegistrationModel(name, email, phone, password,false);

                try {
                    validateForm(body);
                    if(userId == "" || userId == null){
                        Call<String> usersResponse = userServiceInterface.registration(body);
                        usersResponse.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Genial!")
                                        .setMessage("Ya eres usuario de Appets")
                                        .show();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                LoginFragment loginFragment = new LoginFragment();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                        .replace(R.id.fragment_container,loginFragment,"LOGIN_FRAGMENT")
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Algo salio mal!")
                                        .setMessage("Error de comunicacion con el servidor")
                                        .show();
                            }
                        });
                    }else{
                        Call<User> updateUserResponse = userServiceInterface.updateUser(token, userId, body);
                        updateUserResponse.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Genial!")
                                        .setMessage("Su usuario se ha actualizado")
                                        .show();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                HomeFragment homeFragment = new HomeFragment();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left,R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth)
                                        .replace(R.id.fragment_container,homeFragment,"HOME_FRAGMENT")
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                                String pass = passwordInput.getText().toString();
                                if(!(pass.isEmpty())){
                                    deleteSesion();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Algo salio mal!")
                                        .setMessage("Error de comunicacion con el servidor")
                                        .show();
                            }
                        });
                    }
                } catch (Exception e) {
                    new AlertDialog.Builder(G.getContext())
                            .setTitle("Algo salio mal!")
                            .setMessage(e.getMessage())
                            .show();
                }
            }

            private void validateForm(RegistrationModel body) throws Exception {
                if(body.UserName == null || body.UserName.isEmpty()) throw new Exception("El nombre no es correcto");
                if(body.Email == null || body.Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(body.Email).matches()) throw new Exception("El email no es correcto");
                if((body.Password == null || body.Password.isEmpty()) && (userId == "" || userId == null)) throw new Exception("La contraseña no es correcta");
                String confirmPassword = confirmPasswordInput.getText().toString();
                if(!body.Password.equals(confirmPassword)) throw new Exception("Las contraseñas no coinciden");
            }
        });

        return  fragment;
    }

    private void getUserInfo(){
        String files [] = getActivity().fileList();
        if(ExistFile(files,"token.txt") && ExistFile(files,"user.txt")){
            try {
                InputStreamReader userFile = new InputStreamReader(getActivity().openFileInput("user.txt"));
                InputStreamReader tokenFile = new InputStreamReader(getActivity().openFileInput("token.txt"));
                BufferedReader bufferedReaderUserFile = new BufferedReader(userFile);
                BufferedReader bufferedReaderTokenFile = new BufferedReader(tokenFile);
                String[] userLine = bufferedReaderUserFile.readLine().split("/");
                String tokenLine = bufferedReaderTokenFile.readLine();
                if(userLine != null && tokenLine != null){
                    userId = userLine[0].trim();
                    token = tokenLine.trim();
                }
                bufferedReaderUserFile.close();
                bufferedReaderTokenFile.close();
                userFile.close();
                tokenFile.close();
                //get user information
                Call<User> getUserResponse = userServiceInterface.getUser(token, userId);
                getUserResponse.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){
                            //veremos manejo de error se obtiene el codigo asi:
                            //response.code();
                        }
                        user = response.body();
                        nameInput.setText(user.getUserName());
                        phoneInput.setText(user.getPhone());
                        emailInput.setText(user.getEmail());
                        emailInput.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

            }catch (IOException e){

            }
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

    private void deleteSesion(){
        String files [] = getActivity().fileList();

        for (int i = 0; i < files.length; i++){
            File dir = getActivity().getFilesDir();
            File file = new File(dir, files[i]);
            file.delete();
        }
    }
}