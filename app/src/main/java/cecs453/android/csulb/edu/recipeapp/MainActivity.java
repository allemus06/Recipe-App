package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Aenah Ramones and Christian Ovid and related XML
 */
public class MainActivity extends AppCompatActivity {

    private String apiURL = "https://api.edamam.com/";
    private String api_key = "ec3ca0c66808fdf12238ac3135b5f3c7"; //TODO: Remove from here
    private String app_id = "79118f0f";
    private AsyncHttpClient client;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser user;
    private String uid;

    private ImageView appLogo;
    private EditText userInput;
    private Button searchButton;
    //private TextView errorTextView;
    private RecyclerView recyclerView;

    RecyclerAdapter adapter;

    private ArrayList<Recipe> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initializeViews();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userInput.getText().toString().isEmpty()) {
                    String searchRecipe = userInput.getText().toString();
                    int resultsRequested = 1; // changed from amount of results to default 1
                    search(searchRecipe, resultsRequested);

                }
            }
        });

    }

    private void initializeViews () {
        appLogo = (ImageView)findViewById(R.id.appLogo);
        userInput = (EditText)findViewById(R.id.userInput);
        searchButton = (Button)findViewById(R.id.continueButton);
        //errorTextView = (TextView)findViewById(R.id.errorTextView);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // we're setting the RecyclerView to display items vertically
        adapter = new RecyclerAdapter(results);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        results = new ArrayList<>();
    }

    private void search(String searchRecipe, int resultsRequested) {
        getHTTPConnection(searchRecipe, resultsRequested);
    }

    private boolean isUserSignedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        return user != null;

    }


    private void getHTTPConnection(String searchRecipe, int resultsRequested) {

        final String relativeURL = "search?q=" + searchRecipe + "&app_id=" + app_id + "&app_key=" + api_key + "&from=0&to=" + Integer.toString(resultsRequested);

        client = new AsyncHttpClient();
        client.get(apiURL + relativeURL, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                //TODO: Add progress bar start
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                EdamamClient.get(relativeURL, null, new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //errorTextView.setText("Failed on JSONObject");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //errorTextView.setText("Failed on JSONArray");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        //errorTextView.setText("Failed on Throwable");
                    }

                    //TODO: use this JSONObject to do things that are not fun :(
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        //mainTV.setText("Succ on first + " + response.toString());

                        try {

                            JSONArray hits = response.getJSONArray("hits");
                            for (int i = 0; i < hits.length(); i++) {
                                Recipe recipeObject = new Recipe();
                                JSONObject recipe = hits.getJSONObject(i).getJSONObject("recipe");

                                String label = recipe.getString("label");
                                recipeObject.setLabel(label);

                                String imgSource = recipe.getString("image");
                                recipeObject.setImageLink(imgSource);

                                int yield = recipe.getInt("yield");
                                recipeObject.setYield(yield);

                                ArrayList<String> dietLabels = covertJAtoAL(recipe.getJSONArray("dietLabels"));
                                recipeObject.setDietLabels(dietLabels);

                                ArrayList<String> healthLabels = covertJAtoAL(recipe.getJSONArray("healthLabels"));
                                recipeObject.setHealthLabels(healthLabels);

                                ArrayList<String> ingredientLines = covertJAtoAL(recipe.getJSONArray("ingredientLines"));
                                recipeObject.setIngredientLines(ingredientLines);

                                double calories = recipe.getDouble("calories");
                                recipeObject.setCalories(calories);

                                double totalWeight = recipe.getDouble("totalWeight");
                                recipeObject.setTotalWeight(totalWeight);

                                JSONObject totalNutrients = recipe.getJSONObject("totalNutrients");
                                HashMap<String, Nutrient> nutrients = new HashMap<>();

                                addNutrient("ENERC_KCAL", totalNutrients, nutrients);
                                addNutrient("FAT", totalNutrients, nutrients);
                                addNutrient("FASAT", totalNutrients, nutrients);
                                addNutrient("FATRN", totalNutrients, nutrients);
                                addNutrient("FAMS", totalNutrients, nutrients);
                                addNutrient("FAPU", totalNutrients, nutrients);
                                addNutrient("CHOCDF", totalNutrients, nutrients);
                                addNutrient("FIBTG", totalNutrients, nutrients);
                                addNutrient("SUGAR", totalNutrients, nutrients);
                                addNutrient("PROCNT", totalNutrients, nutrients);
                                addNutrient("CHOLE", totalNutrients, nutrients);
                                addNutrient("NA", totalNutrients, nutrients);
                                addNutrient("CA", totalNutrients, nutrients);
                                addNutrient("MG", totalNutrients, nutrients);
                                addNutrient("K", totalNutrients, nutrients);
                                addNutrient("FE", totalNutrients, nutrients);
                                addNutrient("ZN", totalNutrients, nutrients);
                                addNutrient("P", totalNutrients, nutrients);
                                addNutrient("VITA_RAE", totalNutrients, nutrients);
                                addNutrient("VITC", totalNutrients, nutrients);
                                addNutrient("THIA", totalNutrients, nutrients);
                                addNutrient("RIBF", totalNutrients, nutrients);
                                addNutrient("NIA", totalNutrients, nutrients);
                                addNutrient("VITB6A", totalNutrients, nutrients);
                                addNutrient("FOLDFE", totalNutrients, nutrients);
                                addNutrient("VITB12", totalNutrients, nutrients);
                                addNutrient("VITD", totalNutrients, nutrients);
                                addNutrient("TOCPHA", totalNutrients, nutrients);
                                addNutrient("VITK1", totalNutrients, nutrients);

                                recipeObject.setNutrients(nutrients);
                                results.add(recipeObject);
                                adapter.notifyDataSetChanged();
                                //errorTextView.setText("Nutrients: " + results.toString());
                            }

                            //mainTV.setText("Hits: " + results.toString());
                            //errorTextView.setMovementMethod(new ScrollingMovementMethod());
                            RecyclerAdapter clickAdapter = new RecyclerAdapter(results);
                            clickAdapter.setItemClickListener(new RecyclerAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    // do something
                                    Intent viewResultIntent = new Intent (MainActivity.this, RecipeDetailActivity.class);
                                    viewResultIntent.putExtra("RecipeSelection", results.get(position));
                                    startActivity(viewResultIntent);
                                }
                            });
                            // set the adapter for the recyclerView titled listOfRecipes
                            recyclerView.setAdapter(clickAdapter);
                            Toast.makeText(MainActivity.this, "Hits: " + clickAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            //errorTextView.setText("Hits: Error");
                            e.printStackTrace();
                        }
                        Log.i("Json return", response.toString());

                        //Intent viewResultIntent = new Intent (MainActivity.this, RecipeDetailActivity.class);
                        //viewResultIntent.putExtra("RecipeSelection", results.get(0));
                        //startActivity(viewResultIntent);

                    }

                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.i("API Request Fail", errorResponse.toString());
                //errorTextView.setText("API Request Fail " + statusCode);
            }



            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    private void addNutrient(String jsonTag, JSONObject totalNutrients, HashMap<String, Nutrient> nutrients) throws JSONException {
        String nutLabel;
        double nutQuantity;
        String nutUnit;

        if (totalNutrients.has(jsonTag)) {
            totalNutrients.getJSONObject(jsonTag);
            nutLabel = totalNutrients.getJSONObject(jsonTag).getString("label");
            nutQuantity = totalNutrients.getJSONObject(jsonTag).getDouble("quantity");
            nutUnit = totalNutrients.getJSONObject(jsonTag).getString("unit");
        } else {
            nutLabel = null;
            nutQuantity = -1;
            nutUnit = null;
        }
        nutrients.put(nutLabel, new Nutrient(nutLabel, nutQuantity, nutUnit));
    }

    private ArrayList<String> covertJAtoAL(JSONArray dietLabels) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < dietLabels.length(); i++) {
            try {
                result.add(dietLabels.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
