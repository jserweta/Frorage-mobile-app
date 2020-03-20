package com.frorage.server.service;

import com.frorage.server.model.*;
import com.frorage.server.payload.ApiResponse;
import com.frorage.server.payload.IngredientStatus;
import com.frorage.server.payload.RecipeAdd;
import com.frorage.server.repository.IngredientRepository;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.repository.ProductRepository;
import com.frorage.server.repository.RecipeRepository;
import com.frorage.server.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private UsersGroupService mockUsersGroupService;

    @Mock
    private RecipeRepository mockRecipeRepository;

    @Mock
    private KitchenRepository mockKitchenRepository;

    @Mock
    private IngredientRepository mockIngredientRepository;

    @Mock
    private ProductRepository mockProductRepository;
    
    private final String UNAUTHORIZED = "User is unauthorized";

    @Test
    void addRecipe_Created_Test() {
        RecipeAdd recipeAdd = new RecipeAdd( 1, "Przepis", "Opis");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByKitchenIdAndRecipeName(1, "Przepis"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = recipeService.addRecipe(recipeAdd, userPrincipal);
        Recipe apiResponse = (Recipe) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getRecipeName(), "Przepis")
        );
    }

    @Test
    void addRecipe_BadRequest_RecipeExists_Test() {
        RecipeAdd recipeAdd = new RecipeAdd( 1, "Przepis", "Opis");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByKitchenIdAndRecipeName(1, "Przepis"))
                .thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        ResponseEntity<?> responseEntity = recipeService.addRecipe(recipeAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getMessage(), "Recipe with this name already exists")
        );
    }

    @Test
    void addRecipe_Forbidden_Test() {
        RecipeAdd recipeAdd = new RecipeAdd( 1, "Przepis", "Opis");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByKitchenIdAndRecipeName(1, "Przepis"))
                .thenReturn(Optional.empty());
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.addRecipe(recipeAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getMessage(), UNAUTHORIZED)
        );
    }

    @Test
    void addRecipe_BadRequest_AddingFailed_Test() {
        RecipeAdd recipeAdd = new RecipeAdd( 1, "Przepis", "Opis");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByKitchenIdAndRecipeName(1, "Przepis"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = recipeService.addRecipe(recipeAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getMessage(), "Adding failed")
        );
    }

    @Test
    void deleteRecipe_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = recipeService.deleteRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals("OK",apiResponse.getMessage())
        );
    }

    @Test
    void deleteRecipe_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.deleteRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void deleteRecipe_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.deleteRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("There is no such recipe",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfRecipesForKitchen_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        List<Recipe> recipeList = new ArrayList<>();
        Recipe recipe = new Recipe();
        Recipe recipe2 = new Recipe();
        recipeList.add(recipe);
        recipeList.add(recipe2);
        when(mockRecipeRepository.findAllByKitchenId(1)).thenReturn(recipeList);
        ResponseEntity<?> responseEntity = recipeService.getListOfRecipesForKitchen(1, userPrincipal);
        List<Recipe> apiResponse = (List<Recipe>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfRecipesForKitchen_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.getListOfRecipesForKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }
    @Test
    void getListOfRecipesForKitchen_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.getListOfRecipesForKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void updateRecipes_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findById(anyInt())).thenReturn(Optional.of(new Recipe(1,1,"TestName", "Test")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = recipeService.updateRecipe(1, new RecipeAdd(1,"TestName", "Test"), userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode()),
                () ->assertEquals("Recipe updated successfully",apiResponse.getMessage())
        );
    }
    @Test
    void updateRecipes_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findById(anyInt())).thenReturn(Optional.of(new Recipe(1,1,"TestName", "Test")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.updateRecipe(1, new RecipeAdd(1,"TestName", "Test"),userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }
    @Test
    void updateRecipes_Bad_Request_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.updateRecipe(1, new RecipeAdd(1,"TestName", "Test"),userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () ->assertEquals("Recipe with this name does not exists",apiResponse.getMessage())
        );
    }

    @Test
    void getIngredientsAmountForRecipe_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup()));
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient(1, 1, "Skladnik1", 1F, "szt"));
        ingredientList.add(new Ingredient(1, 2, "Skladnik2", 1F, "szt"));
        when(mockIngredientRepository.findAllByRecipeId(anyInt())).thenReturn(ingredientList);
        when(mockProductRepository.findByProductId(anyInt())).thenReturn(Optional.of(new Product(1, "produkt", 2F, "szt", null))).thenReturn(Optional.of(new Product(1, "produkt2", 0F, "szt", null)));
        ResponseEntity<?> responseEntity = recipeService.getIngredientsAmountForRecipe(1, userPrincipal);
        IngredientStatus apiResponse = (IngredientStatus) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(1,apiResponse.getAvailableAmount())
        );
    }

    @Test
    void getIngredientsAmountForRecipe_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.getIngredientsAmountForRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void getIngredientsAmountForRecipe_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = recipeService.getIngredientsAmountForRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Recipe not found",apiResponse.getMessage())
        );
    }
}
