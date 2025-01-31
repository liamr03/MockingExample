package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ShoppingCartTest {

    private ShoppingCart cart;

    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    @DisplayName("Should Add Item To Cart")
    void shouldAddItemToCart () {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);

        assertThat(cart.getItems()).contains(item);
    }

    @Test
    void shouldRemoveItemFromCart() {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);
        cart.removeItem(item);

        assertThat(cart.getItems()).doesNotContain(item);
    }
}
