package com.example.appet.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appet.Model.Pet;
import com.example.appet.R;

import java.util.List;

public class MissingPetAdapter extends RecyclerView.Adapter<MissingPetAdapter.ViewHolder>{

    public List<Pet> mPet;
    private OnItemClickListener mListener;

    public MissingPetAdapter(List<Pet> mPet) {
        this.mPet = mPet;
    }

    public interface OnItemClickListener{
        void onMissingClick(int position);
        void onFoundClick(int position);
        void onEditClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public MissingPetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.missing_pet_item, null, false);
        return new MissingPetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissingPetAdapter.ViewHolder holder, int position) {
        holder.pet_name.setText(mPet.get(position).getName());
        holder.pet_image.setImageBitmap(StringToBitMap(mPet.get(position).getImage()));

    }

    @Override
    public int getItemCount() {
        return mPet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView pet_name;
        public ImageView pet_image, foundBottom, found, editBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pet_name = itemView.findViewById(R.id.pet_name);
            pet_image = itemView.findViewById(R.id.pet_img);
            foundBottom = itemView.findViewById(R.id.foundBottom);
            found = itemView.findViewById(R.id.found);
            editBottom = itemView.findViewById(R.id.editBottom);

            foundBottom.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onMissingClick(position);
                        }
                    }
                }
            });
            found.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onFoundClick(position);
                        }
                    }
                }
            });
            editBottom.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onEditClick(position);
                        }
                    }
                }
            });
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
}