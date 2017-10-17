package cecs453.android.csulb.edu.recipeapp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO: Add a class header comment.
 */

public class Recipe {
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
    // TODO: Find out what this does
    private ArrayList<String> cautions;
    /** String of ingredient and amount */
    private ArrayList<String> ingredientLines;
    /** Key: ingredientLines Value: weight in grams */
    private HashMap<String, Double> ingredientWeight;
    /** Amount of calories of the recipe */
    private double calories;
    /** Total weight of the recipe in grams*/
    private double totalWeight;

    public Recipe() {
    }

    public ArrayList<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(ArrayList<String> dietLabels) {
        this.dietLabels = dietLabels;
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

    public ArrayList<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(ArrayList<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public ArrayList<String> getCautions() {
        return cautions;
    }

    public void setCautions(ArrayList<String> cautions) {
        this.cautions = cautions;
    }

    public ArrayList<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(ArrayList<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public HashMap<String, Double> getIngredientWeight() {
        return ingredientWeight;
    }

    public void setIngredientWeight(HashMap<String, Double> ingredientWeight) {
        this.ingredientWeight = ingredientWeight;
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

    @Override
    public String toString() {
        return "Recipe{" +
                "label='" + label + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", yield=" + yield +
                ", dietLabels=" + dietLabels +
                ", healthLabels=" + healthLabels +
                ", cautions=" + cautions +
                ", ingredientLines=" + ingredientLines +
                '}';
    }
}
