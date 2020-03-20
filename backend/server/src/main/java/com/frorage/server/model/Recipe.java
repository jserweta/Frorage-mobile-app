package com.frorage.server.model;

import javax.persistence.*;

/**
 * Class representing table recipe in database
 */
@Entity
@Table(name = "recipe")
public class Recipe {
    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recipeId;

    @Column(name = "kitchen_id")
    private int kitchenId;

    @Column(name = "recipe_name")
    private String recipeName;

    @Column(name = "recipe_description")
    private String recipeDescription;

    public Recipe(int kitchenId, String recipeName, String recipeDescription) {
        this.kitchenId = kitchenId;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
    }
    public Recipe(int recipeId, int kitchenId, String recipeName, String recipeDescription) {
        this.recipeId = recipeId;
        this.kitchenId = kitchenId;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
    }
    public Recipe() {
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
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
