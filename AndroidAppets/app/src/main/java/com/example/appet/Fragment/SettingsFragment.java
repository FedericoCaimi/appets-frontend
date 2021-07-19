package com.example.appet.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appet.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.bendik.simplerangeview.SimpleRangeView;

public class SettingsFragment extends Fragment {

    ImageView back;
    ImageView confirm;
    SimpleRangeView rangeBar;
    TextView rangeText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_settings, container, false);

        String files [] = getActivity().fileList();
        back = vista.findViewById(R.id.back);
        confirm = vista.findViewById(R.id.check);
        rangeBar = vista.findViewById(R.id.rangeBar);
        rangeText = vista.findViewById(R.id.rangeText);
        rangeText.setText(String.valueOf(rangeBar.getEnd()*10)+" km");

        if(ExistFile(files,"config.txt")){
            try {
                InputStreamReader file = new InputStreamReader(getActivity().openFileInput("config.txt"));
                BufferedReader bufferedReader = new BufferedReader(file);
                String line = bufferedReader.readLine();
                String range = "";
                if(line != null){
                    range = line.trim();
                }
                bufferedReader.close();
                file.close();
                if(!range.equals("")) {
                    rangeBar.setEnd(Integer.parseInt(range));
                    rangeText.setText(String.valueOf(rangeBar.getEnd()*10)+" km");
                }


            }catch (IOException e){

            }

        }

        rangeBar.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
            @Override
            public void onRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i, int i1) {
                i1 = i1*10;
                rangeText.setText(String.valueOf(i1)+" km");
            }
        });

        rangeBar.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
            @Override
            public void onStartRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i) {
                i = i*10;
                rangeText.setText(String.valueOf(i));
            }

            @Override
            public void onEndRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i) {
                i = i*10;
                rangeText.setText(String.valueOf(i)+" km");
            }
        });

        rangeBar.setOnRangeLabelsListener(new SimpleRangeView.OnRangeLabelsListener() {
            @Nullable
            @Override
            public String getLabelTextForPosition(@NotNull SimpleRangeView simpleRangeView, int i, @NotNull SimpleRangeView.State state) {
                return String.valueOf(i);
            }
        });
        // Inflate the layout for this fragment

        back.setOnClickListener(new View.OnClickListener()
        { public void onClick(View v)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth,R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left)
                    .replace(R.id.fragment_container,homeFragment,"HOME_FRAGMENT")
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    /*new AlertDialog.Builder(v.getContext())
                            .setTitle("Error")
                            .setMessage("Error de comunicacion con el servidor")
                            .show();*/
        }
        });

        confirm.setOnClickListener(new View.OnClickListener()
        { public void onClick(View v)
        {
            SaveFile(v);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_rigth,R.anim.exit_left_to_rigth,R.anim.enter_rigth_to_left,R.anim.exit_rigth_to_left)
                    .replace(R.id.fragment_container,homeFragment,"HOME_FRAGMENT")
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    /*new AlertDialog.Builder(v.getContext())
                            .setTitle("Error")
                            .setMessage("Error de comunicacion con el servidor")
                            .show();*/
        }
        });
        return vista;
    }

    private boolean ExistFile(String files [], String fileName){
        for (int i = 0; i < files.length; i++){
            if(fileName.equals(files[i])){
                return true;
            }
        }
        return false;
    }

    public void SaveFile(View view){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("config.txt", Activity.MODE_PRIVATE));
            String range = String.valueOf(rangeBar.getEnd());
            outputStreamWriter.write(range);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }catch (IOException e){

        }
    }
}