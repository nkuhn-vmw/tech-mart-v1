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
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
    public Cart getCart(String ownerId) {
        return cartRepository.findByOwnerId(ownerId)
                .orElseGet(() -> cartRepository.save(new Cart(ownerId)));
    }

    @Override
    @Transactional
    public Cart addItem(String ownerId, Long productId, int quantity) {
        Cart cart = getCart(ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
        // Check if item already exists for this product
        Optional<CartItem> existingOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        CartItem item;
        if (existingOpt.isPresent()) {
            item = existingOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem(cart, product, quantity, product.getPrice());
            cart.addItem(item);
        }
        cartItemRepository.save(item);
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    @Transactional
    public Cart updateItem(String ownerId, Long itemId, int quantity) {
        Cart cart = getCart(ownerId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id " + itemId));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to the specified cart");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Override
    @Transactional
    public void removeItem(String ownerId, Long itemId) {
        Cart cart = getCart(ownerId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id " + itemId));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to the specified cart");
        }
        cart.removeItem(item);
        cartItemRepository.delete(item);
    }

    @Override
    public BigDecimal getCartTotal(String ownerId) {
        Cart cart = getCart(ownerId);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            total = total.add(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }
}
