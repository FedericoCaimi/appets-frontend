package com.jabirdeveloper.tinderswipe;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

import com.example.appet.Model.Post;
import com.example.appet.Model.SimilarPost;

public class CardStackCallback extends DiffUtil.Callback {

    private List<SimilarPost> old, actual;

    public CardStackCallback(List<SimilarPost> old, List<SimilarPost> actual) {
        this.old = old;
        this.actual = actual;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return actual.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getPostImage() == actual.get(newItemPosition).getPostImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == actual.get(newItemPosition);
    }
}
