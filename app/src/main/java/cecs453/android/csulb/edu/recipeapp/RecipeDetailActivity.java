/*Marinela Sanchez*/
package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created By Marinela Sanchez and related XML
 * Additions by Christian Ovid and Alejandro Lemus
 */
public class RecipeDetailActivity extends AppCompatActivity {

    // get the current user of our app so that we can validate and access their information
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    private ImageView recipeImageView;
    private TextView recipeName;
    private TextView ingredientList;
    private TextView calorieVal;
    private TextView yieldVal;
    private Button favoriteButton;
    private Button getRecipe;
    private Recipe recipe;

    // A database reference for handling ratings data in our firebase database
    private DatabaseReference dataBaseRoot;
    private DatabaseReference mDatabaseRatings;
    private DatabaseReference mUsers;
    private DatabaseReference mRecipeReference;

    // this rating bar will display either the user's rating
    //    if the user has not rated the recipe, then display the average rating
    private RatingBar recipeRatingBar;

    // uri of the recipe used for identifying the recipe in the database
    private String uri;
    // the original url of the recipe
    private String URL;
    private float totalRating;
    private int noOfRatings;
    private float initialUserRating;
    private float changedUserRating;
    private boolean favorite;

    // this was added for testing purposes by Chris
    private TextView recipeURL;

    private String DB_FAVORITE = "Favorites";
    Gson gson;
    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        //initialize the database references that we will be using
        dataBaseRoot = FirebaseDatabase.getInstance().getReference();
        mDatabaseRatings = FirebaseDatabase.getInstance().getReference("ratings");
        mUsers = FirebaseDatabase.getInstance().getReference("users");

        Intent intent = getIntent();
        recipe = (Recipe)intent.getSerializableExtra("RecipeSelection");

        initializeViews();
        populateViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get the uri of the recipe so that we can use it as a key in our ratings/favorites database
        uri = recipe.getRecipeURI();

        // get the URL of the recipe
        URL = recipe.getRecipeURL();

        // this was added for testing purposes
        //recipeURL.setText(URL);

        // when the user sets their own personal rating,
        //    send the rating to the database
        recipeRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, final boolean fromUser) {
                // create an instance of a rating for the user's rating
                final Rating userRating = new Rating(uri, rating, 1);

                //this is the listener for the user's private ratings data and the public ratings data
                dataBaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRecipeReference = mDatabaseRatings.child(uri);
                        // if the user has a rating for this recipe already, go ahead and update it
                        if(dataSnapshot.child("users").child(currentFirebaseUser.getUid()).child("ratings").child(uri).exists() && fromUser) {
                            initialUserRating = dataSnapshot.child("users").child(currentFirebaseUser.getUid()).child("ratings").child(uri).getValue(Float.class);
                            mUsers.child(currentFirebaseUser.getUid()).child("ratings").child(uri)
                                    .setValue(rating);
                            changedUserRating = rating;
                            // if the user is changing their rating
                            if(initialUserRating > changedUserRating) {
                                mRecipeReference.child("totalRating").setValue(dataSnapshot.child("ratings").child(uri)
                                        .child("totalRating").getValue(Float.class)
                                        - (initialUserRating - changedUserRating));
                            } else if(initialUserRating < changedUserRating) {
                                mRecipeReference.child("totalRating").setValue(dataSnapshot.child("ratings").child(uri)

                                        .child("totalRating").getValue(Float.class)
                                        + (changedUserRating - initialUserRating));
                            }
                            LayerDrawable stars = (LayerDrawable) recipeRatingBar.getProgressDrawable();
                            stars.getDrawable(2).setColorFilter(Color.parseColor("#FDD017"), PorterDuff.Mode.SRC_ATOP);
                        } else if((!dataSnapshot.child("users").child(currentFirebaseUser.getUid()).child("ratings").child(uri).exists()) && fromUser) {
                            mUsers.child(currentFirebaseUser.getUid()).child("ratings").child(uri)
                                    .setValue(rating);
                            // if the recipe exists in the ratings section of firebase and is a new
                            //    unaltered rating from the user, just add to the
                            //        total rating value under 'ratings' and increment no. of ratings by 1
                            if(dataSnapshot.child("ratings").child(uri).exists()) {
                                mRecipeReference.child("noOfRatings").setValue(dataSnapshot.child("ratings").child(uri)
                                        .child("noOfRatings").getValue(Integer.class) + 1);
                                mRecipeReference.child("totalRating").setValue(dataSnapshot.child("ratings").child(uri)
                                        .child("totalRating").getValue(Float.class)
                                        + userRating.getTotalRating());
                            } else if (!dataSnapshot.child("ratings").child(uri).exists()){
                                // a rating does not exist in the public ratings database
                                mDatabaseRatings.child(uri).setValue(userRating);
                            }
                            LayerDrawable stars = (LayerDrawable) recipeRatingBar.getProgressDrawable();
                            stars.getDrawable(2).setColorFilter(Color.parseColor("#FDD017"), PorterDuff.Mode.SRC_ATOP);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
    //Opens browser  with the link to the recipe
    public void openRecipe(View view){
        String recipeLink = recipe.getRecipeURL();
        Uri site = Uri.parse(recipeLink);
        Intent selection = new Intent();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, site);

        if(browserIntent.resolveActivity(getPackageManager())!=null){
            startActivity(browserIntent);
        }
    }
    //Displays the ingredients of the recipe, health labels, and nutrients in the scroll view
    private void populateViews() {
        //Set Recipe image
        new DownloadImageTask(recipeImageView).execute(recipe.getImageLink());
        recipeName.setText(recipe.getLabel());
        ArrayList<String> ingredients = recipe.getIngredientLines();
        ArrayList<String> healthLabels = recipe.getHealthLabels();
        HashMap<String, Nutrient> nutrients = recipe.getNutrients();
        calorieVal.setText(Double.toString(Math.round(recipe.getCalories())));
        yieldVal.setText(Integer.toString(recipe.getYield()));

        ingredientList.setText("Ingredients");

        for (int i = 0; i<ingredients.size(); i++){
            //populate text view with multiple lines
            //https://android--examples.blogspot.com/2015/01/textview-new-line-multiline-in-android.html
                ingredientList.append(System.getProperty("line.separator"));
                ingredientList.append(ingredients.get(i));

        }

        ingredientList.append(System.getProperty("line.separator"));
        ingredientList.append(System.getProperty("line.separator"));
        ingredientList.append("Health Labels: ");
        for (int j = 0; j<healthLabels.size(); j++){
            ingredientList.append(System.getProperty("line.separator"));
            ingredientList.append(healthLabels.get(j));
        }

        ingredientList.append(System.getProperty("line.separator"));
        ingredientList.append(System.getProperty("line.separator"));
        ingredientList.append("Nutrients: ");
        for(Nutrient nutrientInfo: nutrients.values()){
            ingredientList.append(System.getProperty("line.separator"));
            ingredientList.append(nutrientInfo.toString());
        }




        uri = recipe.getRecipeURI();

        // get the average rating of the recipe if there are ratings in the database
        dataBaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(currentFirebaseUser.getUid()).child("ratings").child(uri).exists()) {
                    initialUserRating = dataSnapshot.child("users").child(currentFirebaseUser.getUid()).child("ratings").child(uri).getValue(Float.class);
                    recipeRatingBar.setRating(initialUserRating);
                    LayerDrawable stars = (LayerDrawable) recipeRatingBar.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(Color.parseColor("#FDD017"), PorterDuff.Mode.SRC_ATOP);
                } else if(dataSnapshot.child("ratings").child(uri).exists()) {
                    totalRating = dataSnapshot.child("ratings").child(uri).child("totalRating").getValue(Float.class);
                    noOfRatings = dataSnapshot.child("ratings").child(uri).child("noOfRatings").getValue(Integer.class);
                    recipeRatingBar.setRating(totalRating/noOfRatings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gson = new Gson();
        json = gson.toJson(recipe);

        //Checks if a recipe is already a favorite
        final String recpipeID = recipe.getRecipeURI();
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentFirebaseUser.getUid()).child(DB_FAVORITE).hasChild(recpipeID)) {
                    favorite = true;
                    favoriteButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGold));
                } else {
                    favorite = false;
                    favoriteButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundLight));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the uri of the recipe so that we can use it as a key in our favorites database
                uri = recipe.getRecipeURI();
                if (favorite) {
                    //Currently favorite
                    favorite = false;
                    favoriteButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundLight));
                    mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUsers.child(currentFirebaseUser.getUid()).child(DB_FAVORITE).child(uri).removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    //Currently NOT favorite
                    favorite = true;
                    favoriteButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGold));
                    mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUsers.child(currentFirebaseUser.getUid()).child(DB_FAVORITE).child(uri).setValue(json);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    private void initializeViews() {
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);
        recipeName = (TextView) findViewById(R.id.recipeName);
        ingredientList = (TextView) findViewById(R.id.ingredientList);
        calorieVal = (TextView) findViewById(R.id.caloriesVal);
        yieldVal = (TextView) findViewById(R.id.yieldVal);
        favoriteButton = findViewById(R.id.favoriteButton);
        ingredientList.setMovementMethod(new ScrollingMovementMethod());

        recipeRatingBar = (RatingBar) findViewById(R.id.ratingBarDetail);

        // added for testing purposes
        //recipeURL = (TextView) findViewById(R.id.textViewURL);

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //Bitmap resized = Bitmap.createScaledBitmap(result, 500, 500, true);
            bmImage.setImageBitmap(result);
        }
    }
}
