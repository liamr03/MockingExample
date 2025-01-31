package com.example;

public class ShoppingCart {

    public ShoppingCart() {
    }

    public boolean addToCart(String item, int quantity) {
        if(item.equals("")){
            System.out.println("You don't have any item");
        }
        else if(item == null){
            System.out.println("Item is null");;
        }

        return true;
    }

    public boolean removeFromCart(String item, int quantity) {
        if(item.equals("")){
            System.out.println("You don't have any item");
        }
        else if(item == null){
            System.out.println("Item is null");;
        }

        return true;
    }




}
