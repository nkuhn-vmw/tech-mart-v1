package com.example.service;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.entity.Product;
import com.example.exception.InsufficientStockException;
import com.example.exception.InvalidProductException;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCart(String ownerId) {
        return cartRepository.findByOwnerId(ownerId).orElseGet(() -> {
            Cart cart = new Cart(ownerId);
            return cartRepository.save(cart);
        });
    }

    @Override
    public CartItem addItem(String ownerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidProductException("Product with id " + productId + " not found"));
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product id " + productId);
        }
        Cart cart = getCart(ownerId);
        // Check if item already exists in cart
        Optional<CartItem> existingOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        CartItem cartItem;
        if (existingOpt.isPresent()) {
            cartItem = existingOpt.get();
            int newQty = cartItem.getQuantity() + quantity;
            if (product.getStock() < newQty) {
                throw new InsufficientStockException("Insufficient stock for product id " + productId);
            }
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceSnapshot(product.getPrice());
            cart.addItem(cartItem);
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem updateItemQuantity(String ownerId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Cart cart = getCart(ownerId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("CartItem does not belong to the cart");
        }
        Product product = cartItem.getProduct();
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product id " + product.getId());
        }
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void removeItem(String ownerId, Long cartItemId) {
        Cart cart = getCart(ownerId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("CartItem does not belong to the cart");
        }
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(String ownerId) {
        Cart cart = getCart(ownerId);
        return cart.getItems();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(String ownerId) {
        Cart cart = getCart(ownerId);
        return cart.getItems().stream()
                .map(item -> item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
