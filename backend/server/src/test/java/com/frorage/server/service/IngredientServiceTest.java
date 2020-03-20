package com.frorage.server.service;

import com.frorage.server.model.Ingredient;
import com.frorage.server.model.Product;
import com.frorage.server.model.Recipe;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.ApiResponse;
import com.frorage.server.payload.IngredientAdd;
import com.frorage.server.repository.IngredientRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private UsersGroupService mockUsersGroupService;

    @Mock
    private IngredientRepository mockIngredientRepository;

    @Mock
    private ProductRepository mockProductRepository;

    @Mock
    private RecipeRepository mockRecipeRepository;

    @Test
    void addIngredient_Created_ProductExists_Test() {
        IngredientAdd ingredientAdd = new IngredientAdd(1, "Skladnik", 30F, "unit");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenId(anyString(), anyInt())).thenReturn(Optional.of(new Product(1, "Skladnik", 1F, "unit", null)));

        ResponseEntity<?> responseEntity = ingredientService.addIngredient(1, ingredientAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode()),
                () -> assertEquals(apiResponse.getMessage(), "Ingredient added successfully")
        );
    }

    @Test
    void addIngredient_Created_ProductNotExists_Test() {
        IngredientAdd ingredientAdd = new IngredientAdd(1, "Skladnik", 30F, "unit");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse( anyInt(),anyString(),anyString())).thenReturn(Optional.of(new Product(1, "Skladnik", 1F, "unit", null)));


        ResponseEntity<?> responseEntity = ingredientService.addIngredient(1, ingredientAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode()),
                () -> assertEquals(apiResponse.getMessage(), "Ingredient added successfully")
        );
    }

    @Test
    void addIngredient_BadRequest_Test() {
        IngredientAdd ingredientAdd = new IngredientAdd(1, "Skladnik", 30F, "unit");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenId(anyString(), anyInt())).thenReturn(Optional.empty()).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = ingredientService.addIngredient(1, ingredientAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(apiResponse.getMessage(), "Adding failed")
        );
    }

        @Test
        void addIngredient_Forbidden_Test(){
            IngredientAdd ingredientAdd = new IngredientAdd(1, "Skladnik", 30F, "unit");
            UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
            when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
            ResponseEntity<?> responseEntity = ingredientService.addIngredient(1, ingredientAdd, userPrincipal);
            ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

            assertAll(
                    () -> assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode()),
                    () -> assertEquals(apiResponse.getMessage(), "User is unauthorized")
            );
        }

    @Test
    void deleteIngredient_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockIngredientRepository.findById(anyInt())).thenReturn(Optional.of(new Ingredient(1, 1, 1, "Skladnik", 30F, "unit")));
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = ingredientService.deleteIngredient(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals("OK",apiResponse.getMessage())
        );
    }

    @Test
    void deleteIngredient_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockIngredientRepository.findById(anyInt())).thenReturn(Optional.of(new Ingredient(1, 1, 1, "Skladnik", 30F, "unit")));
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = ingredientService.deleteIngredient(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals("User is unauthorized",apiResponse.getMessage())
        );
    }

    @Test
    void deleteIngredient_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockIngredientRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = ingredientService.deleteIngredient(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("There is no such ingredient",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfIngredientsForRecipe_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        List<Ingredient> ingredientList = new ArrayList<>();
        Ingredient ingr = new Ingredient();
        Ingredient ingr2 = new Ingredient();
        ingredientList.add(ingr);
        ingredientList.add(ingr2);
        when(mockIngredientRepository.findAllByRecipeId(anyInt())).thenReturn(ingredientList);
        ResponseEntity<?> responseEntity = ingredientService.getListOfIngredientsForRecipe(1, userPrincipal);
        List<Ingredient> apiResponse = (List<Ingredient>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfIngredientsForRecipe_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.of(new Recipe(1, 1, "Przepis", "Opis")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = ingredientService.getListOfIngredientsForRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getMessage(),"User is unauthorized")
        );
    }

    @Test
    void getListOfIngredientsForRecipe_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockRecipeRepository.findByRecipeId(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = ingredientService.getListOfIngredientsForRecipe(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals(apiResponse.getMessage(),"Recipe not found")
        );
    }
    }
