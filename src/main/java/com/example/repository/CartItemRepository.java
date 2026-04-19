package com.example.repository;

import com.example.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for {@link CartItem} entities.
 * Provides standard CRUD operations and custom query methods for accessing items by cart or product.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Find all cart items belonging to a specific cart.
     *
     * @param cartId the identifier of the cart
     * @return list of {@link CartItem} associated with the cart
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Find all cart items that reference a specific product.
     *
     * @param productId the identifier of the product
     * @return list of {@link CartItem} referencing the product
     */
    List<CartItem> findByProductId(Long productId);
}
