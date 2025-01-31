package com.example;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<Item, Integer> items;

    public ShoppingCart() {
        items = new HashMap<>();
    }

    public boolean addItem(Item item) {
        if (item == null){
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    public boolean removeFromCart(Item item) {
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




}
