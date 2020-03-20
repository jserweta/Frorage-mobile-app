package com.frorage.server.repository;

import com.frorage.server.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Integer> {
    List<Ingredient> findAllByRecipeId(int recipeId);
}
