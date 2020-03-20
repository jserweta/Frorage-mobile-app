package com.frorage.server.controller;


import com.frorage.server.model.Ingredient;
import com.frorage.server.payload.IngredientAdd;
import com.frorage.server.security.CurrentUser;
import com.frorage.server.security.UserPrincipal;
import com.frorage.server.service.IngredientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "ingredient", tags = {"ingredient"})
@RestController
@RequestMapping("/api")
public class IngredientController {

    private IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }

    @ApiOperation(value = "add ingredient")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/ingredient/{kitchenId}")
    public ResponseEntity<?> addIngredient(@PathVariable int kitchenId, @RequestBody IngredientAdd ingredientAdd, @CurrentUser UserPrincipal userPrincipal){
        return ingredientService.addIngredient(kitchenId, ingredientAdd, userPrincipal);
    }

    @ApiOperation(value = "delete ingredient")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @DeleteMapping("/ingredient/delete/{ingredientId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable(value="ingredientId") int ingredientId, @CurrentUser UserPrincipal userPrincipal){
        return ingredientService.deleteIngredient(ingredientId, userPrincipal);
    }

    @ApiOperation(value = "get list of ingredients for recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Ingredient.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/ingredient/list/{recipeId}")
    public ResponseEntity<?> getListOfRecipesForKitchen(@PathVariable(value="recipeId") int recipeId, @CurrentUser UserPrincipal userPrincipal){
        return ingredientService.getListOfIngredientsForRecipe(recipeId, userPrincipal);
    }
}
