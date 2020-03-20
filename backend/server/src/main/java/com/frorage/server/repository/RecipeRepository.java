package com.frorage.server.repository;

import com.frorage.server.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Integer> {
    Optional<Recipe> findByKitchenIdAndRecipeName(int kitchenId, String recipeName);
    Optional<Recipe> findByRecipeId(int recipeId);
    List<Recipe> findAllByKitchenId(Integer kitchenId);
}
