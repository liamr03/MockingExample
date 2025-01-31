package com.example;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<Item, Integer> items;

    public ShoppingCart() {
        items = new HashMap<>();
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!items.containsKey(item)) {
            throw new IllegalArgumentException("Item not in cart");
        }
        int quantity = items.get(item);
        if (quantity == 1) {
            items.remove(item);
        } else {
            items.put(item, quantity - 1);
        }
    }

    public double calculateTotalPrice() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    public void applyDiscount(double discountRate) {
        items.keySet().forEach(item -> item.setPrice(item.getPrice() * (1 - discountRate)));
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public int getItemQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }

    public void updateQuantity(Item item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (quantity == 0) {
            items.remove(item);
        } else {
            items.put(item, quantity);
        }
    }
}
