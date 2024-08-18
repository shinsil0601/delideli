package kr.co.jhta.app.delideli.user.cart.service;

import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import kr.co.jhta.app.delideli.user.cart.mapper.UserCartMapper;
import kr.co.jhta.app.delideli.user.store.domain.Option;
import kr.co.jhta.app.delideli.user.store.mapper.UserOptionMapper;
import kr.co.jhta.app.delideli.user.store.service.UserOptionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserCartServiceImpl implements UserCartService {

    private final UserCartMapper userCartMapper;
    private final UserOptionService userOptionService;
    private final UserOptionMapper userOptionMapper;

    public UserCartServiceImpl(UserCartMapper userCartMapper, UserOptionService userOptionService, UserOptionMapper userOptionMapper) {
        this.userCartMapper = userCartMapper;
        this.userOptionService = userOptionService;
        this.userOptionMapper = userOptionMapper;
    }

    @Override
    public void addItemToCart(int userKey, int menuKey, int quantity, ArrayList<Integer> selectedOptions) {
        Cart cartItem = new Cart();
        cartItem.setUserKey(userKey);
        cartItem.setMenuKey(menuKey);
        cartItem.setQuantity(quantity);

        // 장바구니 항목을 먼저 추가하고 자동 생성된 cart_key를 가져옴
        userCartMapper.insertCartItem(cartItem);

        // 추가된 장바구니 항목의 키를 사용해 옵션들을 추가
        for (Integer optionKey : selectedOptions) {
            Option option = userOptionService.getOptionById(optionKey);

            CartOptions cartOption = new CartOptions();
            cartOption.setCartKey(cartItem.getCartKey());  // 생성된 cart_key를 설정
            cartOption.setOptionKey(optionKey.intValue());
            cartOption.setOptionPrice(option.getOptionPrice());
            cartOption.setOptionName(option.getOptionName());

            userCartMapper.insertCartOption(cartOption);
        }
    }

    @Override
    public ArrayList<Cart> getCartItemsByUser(int userKey) {
        return userCartMapper.getCartItemsByUser(userKey);
    }

    @Override
    public Cart getCartItemById(int cartKey) {
        return userCartMapper.getCartItemById(cartKey);
    }

    @Override
    public void updateCartItem(int cartKey, int quantity, ArrayList<Integer> selectedOptionKeys) {
        // 장바구니 항목 수량 업데이트
        userCartMapper.updateCartItemQuantity(cartKey, quantity);

        // 기존 옵션 삭제
        userCartMapper.deleteCartOptions(cartKey);

        // 새로운 옵션 추가
        for (int optionKey : selectedOptionKeys) {
            Option option = userOptionMapper.getOptionById(optionKey);

            CartOptions cartOption = new CartOptions();
            cartOption.setCartKey(cartKey);
            cartOption.setOptionKey(optionKey);
            cartOption.setOptionPrice(option.getOptionPrice());
            cartOption.setOptionName(option.getOptionName());

            userCartMapper.insertCartOption(cartOption);
        }
    }

    @Override
    public void deleteCartItem(int cartKey) {
        userCartMapper.deleteCartItem(cartKey);
    }

    @Override
    public ArrayList<Cart> getCartItemsByStoreInfoKey(int userKey, int storeInfoKey) {
        Map<String, Integer> map = new HashMap<>();
        map.put("userKey", userKey);
        map.put("storeInfoKey", storeInfoKey);
        return userCartMapper.findByStoreInfoKey(map);
    }

    @Override
    public void removeCartItem(int cartKey) {
        userCartMapper.deleteCartOptions(cartKey);
        userCartMapper.deleteCartItem(cartKey);
    }

    @Override
    public int addCart(int userKey, int menuKey, int quantity) {
        Cart cart = new Cart();
        cart.setUserKey(userKey);
        cart.setMenuKey(menuKey);
        cart.setQuantity(quantity);

        // cart 테이블에 데이터를 삽입
        userCartMapper.insertCartItem(cart);

        // 삽입된 cart_key 반환
        return cart.getCartKey();
    }

    @Override
    public void addCartOption(int cartKey, int optionKey, int optionPrice, String optionName) {
        CartOptions cartOption = new CartOptions();
        cartOption.setCartKey(cartKey);
        cartOption.setOptionKey(optionKey);
        cartOption.setOptionPrice(optionPrice);
        cartOption.setOptionName(optionName);

        userCartMapper.insertCartOption(cartOption);
    }

}