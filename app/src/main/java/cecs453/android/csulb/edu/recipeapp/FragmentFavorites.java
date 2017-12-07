package cecs453.android.csulb.edu.recipeapp;

/**
 * Created by aenah modified by Alejandro
 *
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FragmentFavorites extends Fragment {

    // get the current user of our app so that we can validate and access their information
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference favoriteReference;

    private ArrayList<Recipe> favorites;
    private String DB_FAVORITE = "Favorites";
    private Gson gson;
    private String json;

    public FragmentFavorites() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        favorites = new ArrayList<>();
        gson = new Gson();
        loadFavorites();
        initializeViews(rootView);
        return rootView;
    }

    private void loadFavorites() {
        favoriteReference = FirebaseDatabase.getInstance().getReference("users").child(currentFirebaseUser.getUid()).child(DB_FAVORITE);

        favoriteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO: Read all the json files from the children
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    json = child.getValue().toString();
                    Log.i("Favorite Recipe", json);
                    Recipe favRecipe = gson.fromJson(json, Recipe.class);
                    favorites.add(favRecipe);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.favoritesRecView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(favorites);
        recyclerView.setAdapter(adapter);
    }

}
