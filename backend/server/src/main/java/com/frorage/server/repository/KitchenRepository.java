package com.frorage.server.repository;

import com.frorage.server.model.Kitchen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitchenRepository extends JpaRepository<Kitchen,Integer> {

Optional <Kitchen> findByKitchenNameAndKitchenPassword(String name,String password);

}
