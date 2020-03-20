package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;

public class IngredientAdd {

    private int recipeId;

    @NotBlank
    private String ingredientName;

    private Float amount;

    private String unit;

    public IngredientAdd(int recipeId,String ingredientName, Float amount, String unit) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    public IngredientAdd(){}

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
