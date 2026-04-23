package com.example.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import com.example.domain.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

@Entity


public class Cart {

    // Getters and Setters for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Reference to the owning user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifier for the user or session owning this cart
    @Column(nullable = false, unique = true)
    private String ownerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    // Constructors
    public Cart() {}

    public Cart(String ownerId) {
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Convenience methods to manage bi-directional relationship
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}
