package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
 * Created by Aenah Ramones, Christian Ovid and Alejandro Lemus and related XML
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

    private EditText userInput;
    private Button searchButton;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter clickAdapter;

    private ArrayList<Recipe> results;
    private int prog;


    /**/
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    ListView listView;
    /**/

    private float totalRating;
    private int noOfRatings;
    private DatabaseReference mDatabaseRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize the database reference that we will be using for getting ratings
        mDatabaseRatings = FirebaseDatabase.getInstance().getReference("ratings");

        // initialize results as a new ArrayList to store our search results
        results = new ArrayList<>();

        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();
        initializeViews();
        progressBar.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userInput.getText().toString().isEmpty()) {
                    String searchRecipe = userInput.getText().toString();
                    int resultsRequested = 100; // changed from amount of results to default 1
                    search(searchRecipe, resultsRequested);
                }
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.search_icon, "Search Recipe");
        drawerItem[1] = new DataModel(R.drawable.ic_action_name, "Find Nearest Stores");
        drawerItem[2] = new DataModel(R.drawable.heart_icon, "Favorites");
        drawerItem[3] = new DataModel(R.drawable.question_icon, "FAQ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupDrawerToggle();
    }


    private void initializeViews () {
        userInput = (EditText)findViewById(R.id.userInput);
        searchButton = (Button)findViewById(R.id.continueButton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // we're setting the RecyclerView to display items vertically
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(results);
        recyclerView.setAdapter(adapter);


        //list view of the navigation drawer
        listView = (ListView) findViewById(R.id.left_drawer);
    }

    private void search(String searchRecipe, int resultsRequested) {
        getHTTPConnection(searchRecipe, resultsRequested);
    }

    private boolean isUserSignedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        return user != null;
    }

    /**
     * Created by Alejandro Lemus
     * Crated HHTP connection to get all adamam api calls and JSON parsing
     * @param searchRecipe
     * @param resultsRequested
     */
    private void getHTTPConnection(String searchRecipe, int resultsRequested) {

        final String relativeURL = "search?q=" + searchRecipe + "&app_id=" + app_id + "&app_key=" + api_key + "&from=0&to=" + Integer.toString(resultsRequested);

        client = new AsyncHttpClient();
        client.get(apiURL + relativeURL, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                prog = 0;
                progressBar.setProgress(prog);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                EdamamClient.get(relativeURL, null, new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(), "Search Failed :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(), "Search Failed :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), "Search Failed :(", Toast.LENGTH_SHORT).show();
                    }

                    //TODO: use this JSONObject to do things that are not fun :(
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        clickAdapter = new RecyclerAdapter(null);
                        results.clear();
                        clickAdapter.notifyDataSetChanged();
                        try {

                            JSONArray hits = response.getJSONArray("hits");
                            for (int i = 0; i < hits.length(); i++) {
                                final Recipe recipeObject = new Recipe();
                                JSONObject recipe = hits.getJSONObject(i).getJSONObject("recipe");

                                String label = recipe.getString("label");
                                recipeObject.setLabel(label);

                                String imgSource = recipe.getString("image");
                                recipeObject.setImageLink(imgSource);

                                // recipe URI - Uniform Resource Identifier
                                String recipeSource = recipe.getString("uri");
                                recipeObject.setRecipeURI(recipeSource);

                                // recipe URL - Uniform Resource Locator
                                String recipeURL = recipe.getString("url");
                                recipeObject.setRecipeURL(recipeURL);

                                int yield = recipe.getInt("yield");
                                recipeObject.setYield(yield);

                                ArrayList<String> dietLabels = covertJAtoAL(recipe.getJSONArray("dietLabels"));
                                recipeObject.setDietLabels(dietLabels);

                                if (recipe.has("healthLabels")) {
                                    ArrayList<String> healthLabels = covertJAtoAL(recipe.getJSONArray("healthLabels"));
                                    recipeObject.setHealthLabels(healthLabels);
                                } else {
                                    ArrayList<String> healthLabels = new ArrayList<>();
                                    recipeObject.setHealthLabels(healthLabels);
                                }

                                // get the average rating of the recipe if there are ratings in the database
                                mDatabaseRatings.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(recipeObject.getRecipeURI()).exists()) {
                                            totalRating = dataSnapshot.child(recipeObject.getRecipeURI()).child("totalRating").getValue(Float.class);
                                            noOfRatings = dataSnapshot.child(recipeObject.getRecipeURI()).child("noOfRatings").getValue(Integer.class);
                                            recipeObject.setRating(totalRating/noOfRatings);
                                        }
                                        else
                                            recipeObject.setRating(0);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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
                            }
                            RecyclerAdapter clickAdapter = new RecyclerAdapter(results);
                            // set the adapter for the recyclerView titled listOfRecipes
                            recyclerView.setAdapter(clickAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getApplicationContext(), "Search Failed :( - " + statusCode, Toast.LENGTH_SHORT).show();
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case 1:
                fragment = new FragmentMap();
                break;
            case 2:
                fragment = new FragmentFavorites();
                break;
            case 3:
                fragment = new FragmentFAQ();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }
}
