package cecs453.android.csulb.edu.recipeapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created By Alejandro Lemus
 */

public class Recipe implements Serializable{
    /** Name of the recipe */
    private String label;
    /** Web link to the recipe image */
    private String imageLink;
    /** Number of servings */
    private int yield;
    /** balanced, high-protein, etc. */
    private ArrayList<String> dietLabels;
    /** Health Labels: Vegan, peanut free, etc. */
    private ArrayList<String> healthLabels;
    /** String of ingredient and amount */
    private ArrayList<String> ingredientLines;
    /** Amount of calories of the recipe */
    private double calories;
    /** Total weight of the recipe in grams*/
    private double totalWeight;
    /** The nutrient hashmap of the recipe */
    private HashMap<String, Nutrient> nutrients;

    /** URI of the recipe resource */
    private String recipeURI;

    /** recipe rating */
    private float rating;

    public Recipe() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public ArrayList<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(ArrayList<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public ArrayList<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(ArrayList<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public ArrayList<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(ArrayList<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public HashMap<String, Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(HashMap<String, Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public String getRecipeURI() {
        return recipeURI.substring(recipeURI.indexOf('#') + 1);
    }

    public void setRecipeURI(String recipeURI) {
        this.recipeURI = recipeURI;
    }

    public float getRating() { return rating; }

    public void setRating(float rating) { this.rating = rating;}

    public String getHealthString() {
        String ret = "";
        for (int i = 0; i < this.healthLabels.size(); i++) {
            ret += " ·" + this.healthLabels.get(i);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "label='" + label + "\n" +
                ", imageLink='" + imageLink + "\n" +
                ", yield=" + yield + "\n" +
                ", dietLabels=" + dietLabels + "\n" +
                ", healthLabels=" + healthLabels + "\n" +
                ", ingredientLines=" + ingredientLines + "\n" +
                ", calories=" + calories + "\n" +
                ", totalWeight=" + totalWeight + "\n" +
                ", nutrients=" + nutrients.toString() + "\n" +
                '}';
    }
}
