package com.example;

import com.example.domain.*;
import com.example.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CartEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testCartRelationships() {
        // Create user
        User user = new User("testuser");
        user = userRepository.save(user);

        // Create category
        Category category = new Category("Electronics", "Electronic items");
        category = categoryRepository.save(category);

        // Create product
        Product product = new Product("Phone", "Smartphone", BigDecimal.valueOf(199.99), category);
        product.setStock(10);
        product = productRepository.save(product);

        // Create cart linked to user
        Cart cart = new Cart("owner-123");
        cart.setUser(user);
        cart = cartRepository.save(cart);

        // Add cart item
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPriceSnapshot(product.getPrice());
        item = cartItemRepository.save(item);

        // Verify relationships
        Optional<Cart> fetchedCartOpt = cartRepository.findById(cart.getId());
        assertThat(fetchedCartOpt).isPresent();
        Cart fetchedCart = fetchedCartOpt.get();
        assertThat(fetchedCart.getUser()).isEqualTo(user);
        assertThat(fetchedCart.getItems()).hasSize(1);
        CartItem fetchedItem = fetchedCart.getItems().get(0);
        assertThat(fetchedItem.getProduct()).isEqualTo(product);
        assertThat(fetchedItem.getQuantity()).isEqualTo(2);
        assertThat(fetchedItem.getPriceSnapshot()).isEqualByComparingTo(product.getPrice());
    }
}
