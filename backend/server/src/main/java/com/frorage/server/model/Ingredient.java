package com.frorage.server.model;

import javax.persistence.*;

/**
 * Class representing table ingredients in database
 */
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @Column(name = "ingredient_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ingredientId;

    @Column(name = "recipe_id")
    private int recipeId;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "unit")
    private String unit;

    public Ingredient(int recipeId, int productId, String ingredientName, Float amount, String unit) {
        this.recipeId = recipeId;
        this.productId = productId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    public Ingredient(int ingredientId, int recipeId, int productId, String ingredientName, Float amount, String unit) {
        this.ingredientId = ingredientId;
        this.recipeId = recipeId;
        this.productId = productId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    public Ingredient(){}

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
