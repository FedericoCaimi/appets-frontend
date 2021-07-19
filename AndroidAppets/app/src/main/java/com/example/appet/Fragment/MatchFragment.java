package com.example.appet.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appet.Adapter.CardStackAdapter;
import com.example.appet.Interface.PetInterface;
import com.example.appet.Interface.PostInterface;
import com.example.appet.Model.Pet;
import com.example.appet.Model.Post;
import com.example.appet.Model.SimilarPost;
import com.example.appet.R;
import com.example.appet.Services.PetService;
import com.example.appet.Services.PostService;
import com.jabirdeveloper.tinderswipe.CardStackCallback;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragment extends Fragment {

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private List<SimilarPost> posts;
    private PostInterface callback;
    private String postId, phone;
    private String petId;
    private PetInterface petInterface;
    private TextView noMatchText;
    private static final int REQUEST_CALL = 1;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_match, container, false);
        CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
        noMatchText = view.findViewById(R.id.noMatchText);
        noMatchText.setVisibility(view.INVISIBLE);

        postId = this.getArguments().getString("postId");
        petId = this.getArguments().getString("petId");
        posts = new ArrayList<>();
        callback = new PostService();
        petInterface = new PetService();
        addList();

        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                //Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                final int position = manager.getTopPosition() - 1;
                if (direction == Direction.Right){
                    //Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                    goToMissingIfNoMoreSimilars(position);
                }
                if (direction == Direction.Top){
                    //Toast.makeText(getContext(), "Direction Top", Toast.LENGTH_SHORT).show();

                    phone = posts.get(position).getReporterPhone();
                    makePhoneCall();

                    goToMissingIfNoMoreSimilars(position);
                }
                if (direction == Direction.Left){
                    //Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();

                    Call<Post> ignoreSimilar = callback.ignoreSimilar(postId, posts.get(position).getPostId());
                    ignoreSimilar.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if(!response.isSuccessful()){
                                //veremos manejo de error se obtiene el codigo asi:
                                //response.code();
                            }
                            final Post postRes = response.body();

                            goToMissingIfNoMoreSimilars(position);

                            posts.remove(position);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                        }
                    });
                }
                if (direction == Direction.Bottom){
                    //Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();

                    String ubication = posts.get(position).getUbication();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Intent chooser = Intent.createChooser(intent, "Abrir mapa");

                    String uri = "geo:0,0?q="+ ubication + " (name)";

                    intent.setData(Uri.parse(uri));
                    startActivity(chooser);

                    goToMissingIfNoMoreSimilars(position);
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }

            }

            private void goToMissingIfNoMoreSimilars(int position) {
                if(posts.size() == position + 1) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    MissingFragment missingFragment = new MissingFragment();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_rigth_to_left, R.anim.exit_rigth_to_left, R.anim.enter_left_to_rigth, R.anim.exit_left_to_rigth)
                            .replace(R.id.fragment_container, missingFragment, "MISSING_FRAGMENT")
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Lo sentimos!")
                            .setMessage("No hay mas reportes similares para esa mascota")
                            .show();
                }
            }

            @Override
            public void onCardRewound() {
                //Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                //Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(posts);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    private void paginate() {
        List<SimilarPost> old = adapter.getItems();
        List<SimilarPost> actual = new ArrayList<>(posts);
        CardStackCallback callback = new CardStackCallback(old, actual);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setItems(actual);
        hasil.dispatchUpdatesTo(adapter);
    }

    private void addList() {

        Call<ArrayList<SimilarPost>> callPost = callback.similarPosts(postId);

        callPost.enqueue(new Callback<ArrayList<SimilarPost>>() {
            @Override
            public void onResponse(Call<ArrayList<SimilarPost>> call, Response<ArrayList<SimilarPost>> response) {
                if(!response.isSuccessful()){
                    //veremos manejo de error se obtiene el codigo asi:
                    //response.code();
                }
                ArrayList<SimilarPost> postsRes = response.body();
                if(postsRes != null && postsRes.size() > 0){
                    for(SimilarPost post:postsRes){
                        posts.add(new SimilarPost(post.getPostId(), post.getPostTitle(),post.getPostImage(), post.getDescription(), post.getUbication(), post.getType(), post.getCoincidencePercentage(), post.getReporterPhone()));
                    }
                }
                if(posts.size() <= 0){
                    noMatchText.setVisibility(0);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<SimilarPost>> call, Throwable t) {
            }
        });

    }

    private void makePhoneCall() {
        if(phone != null && phone.trim().length() > 0){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else{
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel: " + phone));
                startActivity(intent);
            }
        }else{
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Lo sentimos!")
                    .setMessage("No se agrego telefono de contacto para esta publicacion...")
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {
                Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}