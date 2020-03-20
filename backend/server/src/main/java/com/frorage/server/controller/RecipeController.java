package com.frorage.server.controller;


import com.frorage.server.model.Recipe;
import com.frorage.server.payload.IngredientStatus;
import com.frorage.server.payload.RecipeAdd;
import com.frorage.server.security.CurrentUser;
import com.frorage.server.security.UserPrincipal;
import com.frorage.server.service.RecipeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "recipe", tags = {"recipe"})
@RestController
@RequestMapping("/api")
public class RecipeController {

    private RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @ApiOperation(value = "add recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Recipe.class),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/recipe")
    public ResponseEntity<?> addProduct(@RequestBody RecipeAdd recipeAdd, @CurrentUser UserPrincipal userPrincipal){
        return recipeService.addRecipe(recipeAdd, userPrincipal);
    }

    @ApiOperation(value = "delete recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @DeleteMapping("/recipe/delete/{recipe_id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable(value="recipe_id") int recipeId, @CurrentUser UserPrincipal userPrincipal){
        return recipeService.deleteRecipe(recipeId, userPrincipal);
    }
    @ApiOperation(value = "get list of recipes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Recipe.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/recipe/list/{kitchenId}")
    public ResponseEntity<?> getListOfRecipesForKitchen(@PathVariable(value="kitchenId") int kitchenId, @CurrentUser UserPrincipal userPrincipal){
        return recipeService.getListOfRecipesForKitchen(kitchenId, userPrincipal);
    }
    @ApiOperation(value = "update recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Updated", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 400, message = "Update failed", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/recipe/update/{recipe_id}")
    public ResponseEntity<?> updateRecipe(@PathVariable(value="recipe_id") int recipeId, @RequestBody RecipeAdd recipeAdd, @CurrentUser UserPrincipal userPrincipal){
        return recipeService.updateRecipe(recipeId, recipeAdd, userPrincipal);
    }

    @ApiOperation(value = "get amount of available and required ingredients for recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = IngredientStatus.class),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/recipe/available_products/{recipe_id}")
    public ResponseEntity<?> getIngredientsAmountForRecipe(@PathVariable(value="recipe_id") int recipeId, @CurrentUser UserPrincipal userPrincipal){
        return recipeService.getIngredientsAmountForRecipe(recipeId, userPrincipal);
    }
}
