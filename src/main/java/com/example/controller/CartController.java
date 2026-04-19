package com.example.controller;

import com.example.domain.Cart;
import com.example.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Helper DTOs
    public static class AddItemRequest {
        @NotNull
        private Long productId;
        @Min(1)
        private int quantity;
        // getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class UpdateItemRequest {
        @Min(1)
        private int quantity;
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("X-Owner-Id") String ownerId) {
        Cart cart = cartService.getCart(ownerId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(@RequestHeader("X-Owner-Id") String ownerId,
                                        @Valid @RequestBody AddItemRequest request) {
        Cart cart = cartService.addItem(ownerId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItem(@RequestHeader("X-Owner-Id") String ownerId,
                                           @PathVariable Long itemId,
                                           @Valid @RequestBody UpdateItemRequest request) {
        Cart cart = cartService.updateItem(ownerId, itemId, request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Owner-Id") String ownerId,
                                           @PathVariable Long itemId) {
        cartService.removeItem(ownerId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestHeader("X-Owner-Id") String ownerId) {
        BigDecimal total = cartService.getCartTotal(ownerId);
        return ResponseEntity.ok(total);
    }
}
