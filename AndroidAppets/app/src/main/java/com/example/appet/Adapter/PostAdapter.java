package com.example.appet.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appet.Model.Post;
import com.example.appet.Model.Tag;
import com.example.appet.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public List<Post> mPost;
    private OnItemClickListener mListener;
    View view;

    public PostAdapter( List<Post> mPost) {
        //this.mContext = mContext;
        this.mPost = mPost;
    }

    public interface OnItemClickListener{
        void onMapClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, null, false);
        return new PostAdapter.ViewHolder(view);
    }

    public void addNewPosts(List<Post> posts)
    {
        this.mPost.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.post_image.setImageResource(mPost.get(position).getPostImage());
        Bitmap bitmapImage = StringToBitMap(mPost.get(position).getPostImage());
        holder.post_image.setImageBitmap(bitmapImage);
        holder.post_text.setText(mPost.get(position).getDescription());
        holder.post_title.setText(mPost.get(position).getPostTitle());

        String animal = "";
        String breed = "";
        int RGB = 0;

        List<Tag> tagList = mPost.get(position).getTags();
        int redColor = 0;
        int greenColor = 0;
        int blueColor = 0;
        if(tagList != null && tagList.size() > 0) {
            for (Tag tag : tagList) {
                switch (tag.getType()) {
                    case "Animal":
                        animal = tag.getValue();
                        break;
                    case "Breed":
                        breed = tag.getValue();
                        break;
                    case "RedColor":
                        redColor = Integer.parseInt(tag.getValue());
                        break;
                    case "GreenColor":
                        greenColor = Integer.parseInt(tag.getValue());
                        break;
                    case "BlueColor":
                        blueColor = Integer.parseInt(tag.getValue());
                        break;
                }
            }
            RGB = Color.rgb(redColor, greenColor, blueColor);
        }

        if(tagList != null && tagList.size() > 0) {
            holder.tagAnimal.setText(animal);
            holder.tagBreed.setText(breed);
            holder.tagColor.setBackgroundColor(RGB);
        } else {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.parent);
            layout.removeView(view.findViewById(R.id.tags));
        }

        int red = Color.parseColor("#f06e6e");
        int green = Color.parseColor("#85ed96");

        String title = mPost.get(position).getPostTitle();
        int type = mPost.get(position).getType();

        switch (type){
            case 0:
                holder.title.setBackgroundResource(R.drawable.post_red);
                holder.title.setText("Perdida");
                break;
            case 1:
                holder.title.setBackgroundResource(R.drawable.post_yellow);
                holder.title.setText("Vista");
                break;
            case 2:
                holder.title.setBackgroundResource(R.drawable.post_green);
                holder.title.setText("Encontrada");
                break;
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

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public  TextView post_title;
        public  TextView title;
        public ImageView map;
        public ImageView post_image;
        public TextView post_text;
        public Button tagAnimal, tagBreed, tagColor;
        /*public TextView tagBreed;
        public TextView tagColor;*/
        public LinearLayout post_color;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_title = itemView.findViewById(R.id.post_title);
            title = itemView.findViewById(R.id.title);
            post_image = itemView.findViewById(R.id.post_image);
            post_text = itemView.findViewById(R.id.description);
            tagAnimal = itemView.findViewById(R.id.tagAnimal);
            tagBreed = itemView.findViewById(R.id.tagBreed);
            tagColor = itemView.findViewById(R.id.tagColor);
            post_color = itemView.findViewById(R.id.color);
            map = itemView.findViewById(R.id.map);

            tagAnimal.setBackgroundColor(Color.WHITE);
            tagBreed.setBackgroundColor(Color.WHITE);
            tagColor.setBackgroundColor(Color.WHITE);

            map.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onMapClick(position);
                        }
                    }
                }
            });
        }
    }
}