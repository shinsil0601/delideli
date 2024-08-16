package kr.co.jhta.app.delideli.user.cart.mapper;

import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface CartMapper {
    ArrayList<Cart> getCartItemsByUser(int userKey);

    void insertCartItem(Cart cartItem);

    void insertCartOption(CartOptions cartOption);

    void deleteCartItem(int userKey);

    Cart getCartItemById(int cartKey);

    void updateCartItemQuantity(int cartKey, int quantity);

    void deleteCartOptions(int cartKey);

    ArrayList<Cart> findByStoreInfoKey(Map<String, Integer> map);

}

