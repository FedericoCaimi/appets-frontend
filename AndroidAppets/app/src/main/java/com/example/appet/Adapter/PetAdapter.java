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

import com.example.appet.Fragment.MissingFragment;
import com.example.appet.Model.Pet;
import com.example.appet.R;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder>{

    public List<Pet> mPet;
    private OnItemClickListener mListener;

    public PetAdapter(List<Pet> mPet) {
        this.mPet = mPet;
    }

    public interface OnItemClickListener{
        void onMissingClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public PetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item, null, false);
        return new PetAdapter.ViewHolder(view);
    }

    public void addNewPosts(List<Pet> pets)
    {
        this.mPet.addAll(pets);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.ViewHolder holder, int position) {
        holder.pet_name.setText(mPet.get(position).getName());
        holder.pet_image.setImageBitmap(StringToBitMap(mPet.get(position).getImage()));
    }

    @Override
    public int getItemCount() {
        return mPet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView pet_name;
        public ImageView pet_image, missingBottom, editBottom, deleteBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pet_name = itemView.findViewById(R.id.pet_name);
            pet_image = itemView.findViewById(R.id.pet_img);
            missingBottom = itemView.findViewById(R.id.missingBottom);
            editBottom = itemView.findViewById(R.id.editBottom);
            deleteBottom = itemView.findViewById(R.id.deleteBottom);

            missingBottom.setOnClickListener(new View.OnClickListener(){
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

            deleteBottom.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onDeleteClick(position);
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