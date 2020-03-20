package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;

public class RecipeAdd {

    private int kitchenId;

    @NotBlank
    private String recipeName;

    private String recipeDescription;

    public RecipeAdd(int kitchenId, String recipeName, String recipeDescription) {
        this.kitchenId = kitchenId;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
    }

    public int getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(int kitchenId) {
        this.kitchenId = kitchenId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }
}
