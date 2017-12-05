/*Marinela Sanchez*/
package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created By Marinela Sanchez and related XML
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeName;
    private TextView ingredientList;
    private Recipe recipe;

    // A database reference for handling ratings data in our firebase database
    private DatabaseReference mDatabaseRatings;
    private DatabaseReference mRecipeReference;

    // this rating bar will display either the user's rating
    //    if the user has not rated the recipe, then display the average rating
    private RatingBar recipeRatingBar;

    // uri of the recipe used for identifying the recipe in the database
    private String uri;
    private float totalRating;
    private int noOfRatings;

    // this was added for testing purposes by Chris
    private TextView recipeURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        //initialize the database reference that we will be using
        mDatabaseRatings = FirebaseDatabase.getInstance().getReference("ratings");

        Intent intent = getIntent();
        recipe = (Recipe)intent.getSerializableExtra("RecipeSelection");

        initializeViews();
        populateViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get the uri of the recipe so that we can use it as a key in our ratings database
        uri = recipe.getRecipeURI();
        uri = uri.substring(uri.indexOf('#') + 1);

        // this was added for testing purposes
        recipeURI.setText(uri);

        // when the user sets their own personal rating,
        //    send the rating to the database
        recipeRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, final boolean fromUser) {
                // create an instance of a rating for the user's rating
                final Rating userRating = new Rating(uri, rating, 1);

                mDatabaseRatings.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(uri).exists() && fromUser) {
                            // if the recipe exists in the ratings section of firebase, just add to the
                            //    total rating value under 'ratings' and increment no. of ratings by 1
                            mRecipeReference = mDatabaseRatings.child(uri);
                            mRecipeReference.child("noOfRatings").setValue(dataSnapshot.child(uri)
                                    .child("noOfRatings").getValue(Integer.class) + 1);
                            mRecipeReference.child("totalRating").setValue(dataSnapshot.child(uri)
                                    .child("totalRating").getValue(Float.class)
                                    + userRating.getTotalRating());
                        } else if(!dataSnapshot.child(uri).exists()){
                            // else if the recipe does not exist under the ratings section of firebase,
                            //    add the recipe to the ratings section and set the rating value to the
                            //        input rating and set the no. of ratings to 1
                            // this adds a child node under "ratings" in our database with the recipe's uri
                            //    along with its very first rating
                            mDatabaseRatings.child(uri).setValue(userRating);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void populateViews() {
        //Set Recipe image
        new DownloadImageTask(recipeImageView).execute(recipe.getImageLink());
        recipeName.setText(recipe.getLabel());
        ArrayList<String> ingredients = recipe.getIngredientLines();
        for (int i = 0; i<ingredients.size(); i++){
            //populate text view with multiple lines
            //https://android--examples.blogspot.com/2015/01/textview-new-line-multiline-in-android.html
            if(i == 0){
                ingredientList.setText(ingredients.get(i));
            }
            else{
                ingredientList.append(System.getProperty("line.separator"));
                ingredientList.append(ingredients.get(i));
            }
        }

        uri = recipe.getRecipeURI();
        uri = uri.substring(uri.indexOf('#') + 1);

        // get the average rating of the recipe if there are ratings in the database
        mDatabaseRatings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uri).exists()) {
                    totalRating = dataSnapshot.child(uri).child("totalRating").getValue(Float.class);
                    noOfRatings = dataSnapshot.child(uri).child("noOfRatings").getValue(Integer.class);
                    recipeRatingBar.setRating(totalRating/noOfRatings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initializeViews() {
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);
        recipeName = (TextView) findViewById(R.id.recipeName);
        ingredientList = (TextView) findViewById(R.id.ingredientList);
        ingredientList.setMovementMethod(new ScrollingMovementMethod());

        recipeRatingBar = (RatingBar) findViewById(R.id.ratingBarDetail);

        // added for testing purposes
        recipeURI = (TextView) findViewById(R.id.textViewURI);

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
