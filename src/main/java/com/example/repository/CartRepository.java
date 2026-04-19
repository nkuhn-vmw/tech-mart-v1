package com.example.repository;

import com.example.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for {@link Cart} entities.
 * Provides standard CRUD operations and a method to find a cart by its owner identifier.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Find a cart by the owner identifier (userId or sessionId).
     *
     * @param ownerId the identifier of the owner
     * @return an {@link Optional} containing the {@link Cart} if found, otherwise empty
     */
    Optional<Cart> findByOwnerId(String ownerId);
}
