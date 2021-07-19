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

import com.example.appet.Model.Post;
import com.example.appet.Model.SimilarPost;
import com.example.appet.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    private List<SimilarPost> items;

    public CardStackAdapter(List<SimilarPost> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, age, city;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            city = itemView.findViewById(R.id.item_city);
        }

        public void setData(SimilarPost post) {
            //Picasso.get().load(post.getPostImage()).fit().centerCrop().into(image);
            Bitmap bitmapImage = StringToBitMap(post.getPostImage());
            image.setImageBitmap(bitmapImage);
            name.setText(post.getPostTitle());
            String matchPercentage = post.getCoincidencePercentage() + "% de coincidencia";
            age.setText(matchPercentage);
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

    public List<SimilarPost> getItems() {
        return items;
    }

    public void setItems(List<SimilarPost> items) {
        this.items = items;
    }
}
