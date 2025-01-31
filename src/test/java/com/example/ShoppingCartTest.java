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
    @DisplayName("Should Remove Item From Cart")
    void shouldRemoveItemFromCart() {
        Item item = new Item("item1", 10.0);
        cart.addToCart(item);
        cart.removeFromCart(item);

        assertThat(cart.getItems()).doesNotContain(item);
    }

    @Test
    @DisplayName("Should Calculate Total Price of Items In Cart")
    void shouldCalculateTotalPrice() {
        Item item1 = new Item("item1", 10.0);
        Item item2 = new Item("item2", 15.0);
        cart.addItem(item1);
        cart.addItem(item2);

        double totalPrice = cart.calculateTotalPrice();

        assertThat(totalPrice).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Should Update Item Quantity")
    void shouldUpdateItemQuantity() {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);
        cart.updateQuantity(item, 5);

        int quantity = cart.getItemQuantity(item);

        assertThat(quantity).isEqualTo(5);
    }

    @Test
    @DisplayName("Should Throw Exception For Null Items")
    void shouldThrowExceptionForNullItem() {
        assertThatThrownBy(() -> cart.addItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item cannot be null");
    }
}
