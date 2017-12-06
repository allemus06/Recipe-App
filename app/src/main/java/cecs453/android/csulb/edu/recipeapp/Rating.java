package cecs453.android.csulb.edu.recipeapp;

/**
 * Created by Christian Ovid
 * Description: This is the rating class that will be used to define rating objects.
 */

public class Rating {

    private String recipeID;
    private float totalRating;
    private int noOfRatings;

    public Rating(){

    }

    public Rating(String recipeID, float totalRating, int noOfRatings) {
        this.recipeID = recipeID;
        this.totalRating = totalRating;
        this.noOfRatings = noOfRatings;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public float getTotalRating() {
        return totalRating;
    }

    public int getNoOfRatings() {
        return noOfRatings;
    }
}
