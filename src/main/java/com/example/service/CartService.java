package com.example.service;

import java.math.BigDecimal;

public interface CartService {
    /**
     * Add a product to the cart for the given owner. If the product already exists in the cart, the quantity is increased.
     */
    void addCartItem(String ownerId, Long productId, int quantity);

    /**
     * Remove a product from the cart.
     */
    void removeCartItem(String ownerId, Long productId);

    /**
     * Update the quantity of a product in the cart.
     */
    void updateQuantity(String ownerId, Long productId, int quantity);

    /**
     * Calculate the total price of all items in the cart.
     */
    BigDecimal calculateTotal(String ownerId);
}
