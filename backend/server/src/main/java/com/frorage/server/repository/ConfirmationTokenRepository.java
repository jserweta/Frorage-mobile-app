package com.frorage.server.repository;

import com.frorage.server.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    List<ConfirmationToken> findByToken(String token);
    Optional<ConfirmationToken> findByUserId(Long id);
}
