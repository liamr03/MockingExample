package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    @DisplayName("Should Add Item To Cart")
    void shouldAddItemToCart () {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);

        assertThat(cart.getItems()).containsKey(item); // Check if the key is present
    }

    @Test
    @DisplayName("Should Remove Item From Cart")
    void shouldRemoveItemFromCart() {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);
        cart.removeItem(item);

        assertThat(cart.getItems()).doesNotContainKey(item); // Check if the key is removed
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

    @Test
    @DisplayName("Should Apply Discount Correctly")
    void shouldApplyDiscountCorrectly() {
        Item item = new Item("item1", 100.0);
        cart.addItem(item);
        cart.applyDiscount(0.1); // 10% discount

        double totalPrice = cart.calculateTotalPrice();

        assertThat(totalPrice).isEqualTo(90.0); // Price after discount
    }

    @Test
    @DisplayName("Should Throw Exception When Removing Null Item")
    void shouldThrowExceptionWhenRemovingNullItem() {
        assertThatThrownBy(() -> cart.removeItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item cannot be null");
    }

    @Test
    @DisplayName("Should Throw Exception When Removing Item Not In Cart")
    void shouldThrowExceptionWhenRemovingItemNotInCart() {
        Item item = new Item("item1", 10.0);
        assertThatThrownBy(() -> cart.removeItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item not in cart");
    }

    @Test
    @DisplayName("Should Throw Exception When Updating Null Item")
    void shouldThrowExceptionWhenUpdatingNullItem() {
        assertThatThrownBy(() -> cart.updateQuantity(null, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item cannot be null");
    }

    @Test
    @DisplayName("Should Throw Exception When Updating Item With Negative Quantity")
    void shouldThrowExceptionWhenUpdatingItemWithNegativeQuantity() {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);
        assertThatThrownBy(() -> cart.updateQuantity(item, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity cannot be negative");
    }

    @Test
    @DisplayName("Should Remove Item When Updating Quantity to Zero")
    void shouldRemoveItemWhenUpdatingQuantityToZero() {
        Item item = new Item("item1", 10.0);
        cart.addItem(item);
        cart.updateQuantity(item, 0);

        assertThat(cart.getItems()).doesNotContainKey(item); // Check if the key is removed
    }
}
