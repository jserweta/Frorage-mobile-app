package com.frorage.server.service;

import com.frorage.server.model.*;
import com.frorage.server.payload.IngredientStatus;
import com.frorage.server.payload.RecipeAdd;
import com.frorage.server.repository.IngredientRepository;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.repository.ProductRepository;
import com.frorage.server.repository.RecipeRepository;
import com.frorage.server.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    
    private final String UNAUTHORIZED = "User is unauthorized";

    private RecipeRepository recipeRepository;
    private UsersGroupService usersGroupService;
    private KitchenRepository kitchenRepository;
    private IngredientRepository ingredientRepository;
    private ProductRepository productRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UsersGroupService usersGroupService, KitchenRepository kitchenRepository, IngredientRepository ingredientRepository, ProductRepository productRepository){
        this.recipeRepository = recipeRepository;
        this.usersGroupService = usersGroupService;
        this.kitchenRepository = kitchenRepository;
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<?> addRecipe(RecipeAdd recipe, UserPrincipal userPrincipal){
        Recipe r = new Recipe(recipe.getKitchenId(), recipe.getRecipeName(), recipe.getRecipeDescription());
        Optional<Recipe> recipeOptional = recipeRepository.findByKitchenIdAndRecipeName(recipe.getKitchenId(), recipe.getRecipeName());
        if(recipeOptional.isPresent()){
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Recipe with this name already exists"), HttpStatus.BAD_REQUEST);
        }else{
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipe.getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                recipeRepository.save(r);
                Optional<Recipe> recipeWithId = recipeRepository.findByKitchenIdAndRecipeName(r.getKitchenId(), r.getRecipeName());
                if(recipeWithId.isPresent()){
                    return new ResponseEntity<>(recipeWithId.get(), HttpStatus.CREATED);
                }
                else{
                    return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Adding failed"), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
    }

    public ResponseEntity<?> deleteRecipe(int recipeId, UserPrincipal userPrincipal){
        Optional<Recipe> recipeOptional = recipeRepository.findByRecipeId(recipeId);
        if(recipeOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipeOptional.get().getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                recipeRepository.deleteById(recipeId);
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true,"OK"),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
        else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"There is no such recipe"),HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<?> getListOfRecipesForKitchen(int kitchenId, UserPrincipal userPrincipal){
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(kitchenId);
        if(kitchenOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(recipeRepository.findAllByKitchenId(kitchenId), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Kitchen not found"),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateRecipe(int recipeId, RecipeAdd recipeAdd, UserPrincipal userPrincipal){
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if( recipeOptional.isEmpty()){
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Recipe with this name does not exists"), HttpStatus.BAD_REQUEST);
        }else{
            Recipe recipe = recipeOptional.get();
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipe.getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                recipe.setRecipeName(recipeAdd.getRecipeName());
                recipe.setRecipeDescription(recipeAdd.getRecipeDescription());
                recipeRepository.save(recipe);
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true, "Recipe updated successfully"), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
    }

    public ResponseEntity<?> getIngredientsAmountForRecipe(int recipeId, UserPrincipal userPrincipal){
        Optional<Recipe> recipeOptional = recipeRepository.findByRecipeId(recipeId);
        List<Ingredient> required;
        int requiredAmount;
        int availableAmount = 0;
        if(recipeOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(recipeOptional.get().getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                required = ingredientRepository.findAllByRecipeId(recipeId);
                requiredAmount = required.size();
                for(Ingredient i : required){
                    Optional<Product> productOptional = productRepository.findByProductId(i.getProductId());
                    if(productOptional.isPresent()){
                        if(productOptional.get().getAmount() > i.getAmount()){
                            availableAmount += 1;
                        }
                    }
                }
                return new ResponseEntity<>(new IngredientStatus(availableAmount, requiredAmount), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Recipe not found"),HttpStatus.NOT_FOUND);
    }
}
