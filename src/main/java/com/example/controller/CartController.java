package com.example.controller;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.exception.InsufficientStockException;
import com.example.exception.InvalidProductException;
import com.example.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieve the cart for the given owner.
     */
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("X-Owner-Id") String ownerId) {
        Cart cart = cartService.getCart(ownerId);
        return ResponseEntity.ok(cart);
    }

    /**
     * List items in the cart.
     */
    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-Owner-Id") String ownerId) {
        List<CartItem> items = cartService.getCartItems(ownerId);
        return ResponseEntity.ok(items);
    }

    /**
     * Add a new item to the cart.
     */
    @PostMapping("/items")
    public ResponseEntity<CartItem> addItem(
            @RequestHeader("X-Owner-Id") String ownerId,
            @Valid @RequestBody CartItemRequest request) {
        CartItem added = cartService.addItem(ownerId, request.getProductId(), request.getQuantity());
        return new ResponseEntity<>(added, HttpStatus.CREATED);
    }

    /**
     * Update quantity of an existing cart item.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItem> updateItem(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartItem updated = cartService.updateItemQuantity(ownerId, itemId, request.getQuantity());
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove an item from the cart.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long itemId) {
        cartService.removeItem(ownerId, itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get total price of the cart.
     */
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestHeader("X-Owner-Id") String ownerId) {
        BigDecimal total = cartService.getCartTotal(ownerId);
        return ResponseEntity.ok(total);
    }
}
