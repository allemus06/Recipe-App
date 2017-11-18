package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Alejandro Lemus
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.recyclerViewHolder>{

    // Declare an instance of an ArrayList that can contain our recipe search results
    private ArrayList<Recipe> recipeSearchResults;

    // This is a constructor for our adapter. We pass in the ArrayList created in MainActivity
    //    so that our adapter can use the results stored in there
    public RecyclerAdapter(ArrayList<Recipe> recipeList) {
        recipeSearchResults = recipeList;
    }

    // method for creating a view holder
    @Override
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recipe, parent, false);
        return new recyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(recyclerViewHolder viewHolder, int position) {
        // we set the text of the item in our recyclerView to whatever is stored in our label
        viewHolder.recipeNameTextView.setText(recipeSearchResults.get(position).getLabel());
        new DownloadListImageTask(viewHolder.recipeImage).execute(recipeSearchResults.get(position).getImageLink());
    }

    // use this method to get the number of search results
    @Override
    public int getItemCount() {
        return recipeSearchResults.size();
    }

    // This is an inner class that defines the ViewHolder for each item in our RecyclerView
    public class recyclerViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        ImageView recipeImage;

        recyclerViewHolder(final View itemView) {
            super(itemView);
            initSubClassViews();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent recipeDetailsIntent = new Intent(itemView.getContext(), RecipeDetailActivity.class);
                    recipeDetailsIntent.putExtra("RecipeSelection", recipeSearchResults.get(getPosition()));
                    itemView.getContext().startActivity(recipeDetailsIntent);
                }
            });

        }

        private void initSubClassViews() {
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }
    }

    private class DownloadListImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadListImageTask(ImageView bmImage) {
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
