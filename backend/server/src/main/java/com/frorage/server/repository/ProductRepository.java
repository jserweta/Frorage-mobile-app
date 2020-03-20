package com.frorage.server.repository;

import com.frorage.server.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findAllByKitchenIdAndToBuyIsTrue(Integer kitchenId);

    List<Product> findAllByKitchenIdAndToBuyIsFalse(Integer kitchenId);

    List<Product> findAllByKitchenIdAndFavouriteIsTrue(Integer kitchenId);

    List<Product> findAllByKitchenIdAndRunningOutIsTrue(Integer kitchenId);

    @Query("select p from Product p where p.kitchenId = ?1 and p.amount > ?2 and p.toBuy = false")
    List<Product> findAllByKitchenIdAndToBuyIsFalseAndAmountGraterThan(Integer kitchenId, Float amount);

    Optional<Product> findByProductId(int productId);

    Optional<Product> findByProductNameAndKitchenId(String productName, int kitchenId);

    Optional<Product> findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse(Integer kitchenId, String productName, String unit);
    Optional<Product> findByProductNameAndKitchenIdAndToBuyIsTrue(String productName, int kitchenId);
}

