package kr.co.jhta.app.delideli.user.cart.service;

import kr.co.jhta.app.delideli.user.cart.domain.Cart;

import java.util.ArrayList;

public interface CartService {

    void addItemToCart(int userKey, int menuKey, int quantity, ArrayList<Integer> selectedOptions);

    ArrayList<Cart> getCartItemsByUser(int userKey);


    Cart getCartItemById(int cartKey);

    void updateCartItem(int cartKey, int quantity, ArrayList<Integer> selectedOptionKeys);

    void deleteCartItem(int cartKey);

    ArrayList<Cart> getCartItemsByStoreInfoKey(int userKey, int storeInfoKey);
}