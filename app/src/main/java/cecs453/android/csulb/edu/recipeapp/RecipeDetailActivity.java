package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeName;
    private TextView ingredientList;

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        recipe = (Recipe)intent.getSerializableExtra("RecipeSelection");

        initializeViews();
        populateViews();
    }

    private void populateViews() {
        //Set Recipe image
        new DownloadImageTask(recipeImageView).execute(recipe.getImageLink());
        recipeName.setText(recipe.getLabel());
        ArrayList<String> ingredients = recipe.getIngredientLines();
        for (int i = 0; i<ingredients.size(); i++){
            if(i == 0){
                ingredientList.setText(ingredients.get(i));
            }
            else{
                ingredientList.append(System.getProperty("line.separator"));
                ingredientList.append(ingredients.get(i));

            }
        }
    }

    private void initializeViews() {
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);
        recipeName = (TextView) findViewById(R.id.recipeName);
        ingredientList = (TextView) findViewById(R.id.ingredientList);

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
