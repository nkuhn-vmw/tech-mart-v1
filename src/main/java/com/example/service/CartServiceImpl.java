package com.example.service;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Product;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void addCartItem(String ownerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Cart cart = cartRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(ownerId);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            // price snapshot remains same as original
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceSnapshot(product.getPrice());
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public void removeCartItem(String ownerId, Long productId) {
        Cart cart = cartRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for owner"));
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cart.removeItem(itemToRemove);
        cartItemRepository.delete(itemToRemove);
    }

    @Override
    public void updateQuantity(String ownerId, Long productId, int quantity) {
        if (quantity <= 0) {
            // If quantity is zero or negative, remove the item
            removeCartItem(ownerId, productId);
            return;
        }
        Cart cart = cartRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for owner"));
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotal(String ownerId) {
        Cart cart = cartRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for owner"));
        return cart.getItems().stream()
                .map(item -> item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
