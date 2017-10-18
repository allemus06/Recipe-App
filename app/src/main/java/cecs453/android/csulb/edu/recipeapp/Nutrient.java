package cecs453.android.csulb.edu.recipeapp;

import java.io.Serializable;

/**
 * TODO: Add a class header comment.
 */

public class Nutrient implements Serializable{
    private String label;
    private double quantity;
    private String unit;

    public Nutrient() {
    }

    public Nutrient(String label, double quantity, String unit) {
        this.label = label;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Nutrient{" +
                "label='" + label + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}
