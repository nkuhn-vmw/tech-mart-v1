package com.example.service;

import com.example.domain.Cart;
import com.example.domain.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Cart getCart(String ownerId);
    CartItem addItem(String ownerId, Long productId, int quantity);
    CartItem updateItemQuantity(String ownerId, Long cartItemId, int quantity);
    void removeItem(String ownerId, Long cartItemId);
    List<CartItem> getCartItems(String ownerId);
    BigDecimal getCartTotal(String ownerId);
}
