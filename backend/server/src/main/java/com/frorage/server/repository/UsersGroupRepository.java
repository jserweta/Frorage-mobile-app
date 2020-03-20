package com.frorage.server.repository;

import com.frorage.server.model.UsersGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersGroupRepository extends JpaRepository<UsersGroup,Integer> {
    List<UsersGroup> findAllByUserId(Long id);
    Optional<UsersGroup> findByKitchenIdAndUserId(int kitchenId, Long userId);
}
