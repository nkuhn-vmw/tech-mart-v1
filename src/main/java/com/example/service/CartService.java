package com.example.service;

import com.example.domain.Cart;
import java.math.BigDecimal;

public interface CartService {
    Cart getCart(String ownerId);
    Cart addItem(String ownerId, Long productId, int quantity);
    Cart updateItem(String ownerId, Long itemId, int quantity);
    void removeItem(String ownerId, Long itemId);
    BigDecimal getCartTotal(String ownerId);
}
