package com.example.appet.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.appet.Adapter.PostAdapter;
import com.example.appet.Interface.AuthenticationInterface;
import com.example.appet.Interface.LoginCommunicationInterface;
import com.example.appet.Interface.PostInterface;
import com.example.appet.MainActivity;
import com.example.appet.Model.LoginModel;
import com.example.appet.Model.Session;
import com.example.appet.R;
import com.example.appet.Services.AuthenticationService;
import com.example.appet.Services.PostService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    AuthenticationInterface authenticationService;
    Button registrationButton;
    LoginCommunicationInterface LoginCommunicationInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            LoginCommunicationInterface = (LoginCommunicationInterface) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_login, container, false);
        emailInput = fragment.findViewById(R.id.emailInput);
        passwordInput = fragment.findViewById(R.id.passwordInput);
        loginButton = fragment.findViewById(R.id.loginButton);
        registrationButton = fragment.findViewById(R.id.registrationButton);
        authenticationService = new AuthenticationService();
        String files [] = getActivity().fileList();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View G = v;
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                LoginModel body = new LoginModel(email, password);

                try {
                    validateForm(body);

                    Call<Session> sessionResponse = authenticationService.login(body);
                    sessionResponse.enqueue(new Callback<Session>() {
                        @Override
                        public void onResponse(Call<Session> call, Response<Session> response) {
                            Session session = response.body();
                            if (session != null) {
                                SaveFile(session);
                                LoginCommunicationInterface.postLoginEvent();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                MissingFragment missingFragment = new MissingFragment();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_rigth_to_left, R.anim.exit_rigth_to_left, R.anim.enter_left_to_rigth, R.anim.exit_left_to_rigth)
                                        .replace(R.id.fragment_container, missingFragment, "MISSING_FRAGMENT")
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                            } else {
                                new AlertDialog.Builder(G.getContext())
                                        .setTitle("Error al iniciar sesión")
                                        .setMessage("Revise sus datos")
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Session> call, Throwable t) {
                            new AlertDialog.Builder(G.getContext())
                                    .setTitle("Error")
                                    .setMessage("Error de comunicacion con el servidor")
                                    .show();
                        }
                    });
                } catch (Exception e) {
                    new AlertDialog.Builder(G.getContext())
                            .setTitle("Algo salio mal!")
                            .setMessage(e.getMessage())
                            .show();
                }
            }

            private void validateForm(LoginModel body) throws Exception {
                if(body.email == null || body.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(body.email).matches()) throw new Exception("El email no es correcto");
                if(body.password == null || body.password.isEmpty()) throw new Exception("La contraseña no es correcta");
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container,registrationFragment,registrationFragment.getTag()).commit();
            }
        });

        return fragment;
    }

    private boolean ExistFile(String files [], String fileName){
        for (int i = 0; i < files.length; i++){
            if(fileName.equals(files[i])){
                return true;
            }
        }
        return false;
    }

    public void SaveFile(Session session){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("user.txt", Activity.MODE_PRIVATE));
            String userId = session.getUserId();
            String email = emailInput.getText().toString();

            outputStreamWriter.write(userId.trim() + '/' + email.trim());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            OutputStreamWriter outputStreamWriterToken = new OutputStreamWriter(getActivity().openFileOutput("token.txt", Activity.MODE_PRIVATE));
            String token = session.getToken();
            outputStreamWriterToken.write(token.trim());
            outputStreamWriterToken.flush();
            outputStreamWriterToken.close();
        }catch (IOException e){

        }
    }


}