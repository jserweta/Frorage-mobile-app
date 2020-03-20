package com.frorage.server.service;

import com.frorage.server.model.Ingredient;
import com.frorage.server.model.Product;
import com.frorage.server.model.Recipe;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.IngredientAdd;
import com.frorage.server.repository.IngredientRepository;
import com.frorage.server.repository.ProductRepository;
import com.frorage.server.repository.RecipeRepository;
import com.frorage.server.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IngredientService {

    private final String UNAUTHORIZED = "User is unauthorized";

    private IngredientRepository ingredientRepository;
    private ProductRepository productRepository;
    private RecipeRepository recipeRepository;
    private UsersGroupService usersGroupService;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, ProductRepository productRepository, UsersGroupService usersGroupService, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.usersGroupService = usersGroupService;
        this.recipeRepository = recipeRepository;
    }

    public ResponseEntity<?> addIngredient(int kitchenId, IngredientAdd ingredientAdd, UserPrincipal userPrincipal) {
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
        if (usersGroupOptional.isEmpty()) {
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, UNAUTHORIZED), HttpStatus.FORBIDDEN);
        }

        Optional<Product> productWithId = productRepository.findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse(kitchenId, ingredientAdd.getIngredientName(), ingredientAdd.getUnit());
        Ingredient i = null;

        if (productWithId.isPresent()) {
            i = new Ingredient(ingredientAdd.getRecipeId(), productWithId.get().getProductId(), ingredientAdd.getIngredientName(), ingredientAdd.getAmount(), ingredientAdd.getUnit());
        } else {
            Product product = new Product(kitchenId, ingredientAdd.getIngredientName(), 0F, ingredientAdd.getUnit(), null);
            productRepository.save(product);
            productWithId = productRepository.findByProductNameAndKitchenId(product.getProductName(), kitchenId);
            if (productWithId.isPresent()) {
                i = new Ingredient(ingredientAdd.getRecipeId(), productWithId.get().getProductId(), productWithId.get().getProductName(), ingredientAdd.getAmount(), ingredientAdd.getUnit());
            } else {
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "Adding failed"), HttpStatus.BAD_REQUEST);
            }
        }
        ingredientRepository.save(i);
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true, "Ingredient added successfully"), HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteIngredient(int ingredientId, UserPrincipal userPrincipal) {
        Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);
        if (ingredientOptional.isPresent()) {
            Optional<Recipe> recipeOptional = recipeRepository.findByRecipeId(ingredientOptional.get().getRecipeId());
            if (recipeOptional.isPresent()) {
                Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipeOptional.get().getKitchenId(), userPrincipal.getId());
                if (usersGroupOptional.isPresent()) {
                    ingredientRepository.delete(ingredientOptional.get());
                    return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true, "OK"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, UNAUTHORIZED), HttpStatus.FORBIDDEN);
                }
            }
        }
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "There is no such ingredient"), HttpStatus.NOT_FOUND);
    }
    public ResponseEntity<?> getListOfIngredientsForRecipe(int recipeId, UserPrincipal userPrincipal){
        Optional<Recipe> recipeOptional = recipeRepository.findByRecipeId(recipeId);
        if(recipeOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipeOptional.get().getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(ingredientRepository.findAllByRecipeId(recipeId), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Recipe not found"),HttpStatus.NOT_FOUND);
    }
}