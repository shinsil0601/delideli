package kr.co.jhta.app.delideli.user.cart.service;

import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import kr.co.jhta.app.delideli.user.cart.mapper.CartMapper;
import kr.co.jhta.app.delideli.user.store.domain.Option;
import kr.co.jhta.app.delideli.user.store.mapper.OptionMapper;
import kr.co.jhta.app.delideli.user.store.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final OptionService optionService;
    private final OptionMapper optionMapper;

    public CartServiceImpl(CartMapper cartMapper, OptionService optionService, OptionMapper optionMapper) {
        this.cartMapper = cartMapper;
        this.optionService = optionService;
        this.optionMapper = optionMapper;
    }

    @Override
    public void addItemToCart(int userKey, int menuKey, int quantity, ArrayList<Integer> selectedOptions) {
        Cart cartItem = new Cart();
        cartItem.setUserKey(userKey);
        cartItem.setMenuKey(menuKey);
        cartItem.setQuantity(quantity);

        // 장바구니 항목을 먼저 추가하고 자동 생성된 cart_key를 가져옴
        cartMapper.insertCartItem(cartItem);

        // 추가된 장바구니 항목의 키를 사용해 옵션들을 추가
        for (Integer optionKey : selectedOptions) {
            Option option = optionService.getOptionById(optionKey);

            CartOptions cartOption = new CartOptions();
            cartOption.setCartKey(cartItem.getCartKey());  // 생성된 cart_key를 설정
            cartOption.setOptionKey(optionKey.intValue());
            cartOption.setOptionPrice(option.getOptionPrice());
            cartOption.setOptionName(option.getOptionName());

            cartMapper.insertCartOption(cartOption);
        }
    }

    @Override
    public ArrayList<Cart> getCartItemsByUser(int userKey) {
        return cartMapper.getCartItemsByUser(userKey);
    }

    @Override
    public Cart getCartItemById(int cartKey) {
        return cartMapper.getCartItemById(cartKey);
    }

    @Override
    public void updateCartItem(int cartKey, int quantity, ArrayList<Integer> selectedOptionKeys) {
        // 장바구니 항목 수량 업데이트
        cartMapper.updateCartItemQuantity(cartKey, quantity);

        // 기존 옵션 삭제
        cartMapper.deleteCartOptions(cartKey);

        // 새로운 옵션 추가
        for (int optionKey : selectedOptionKeys) {
            Option option = optionMapper.getOptionById(optionKey);

            CartOptions cartOption = new CartOptions();
            cartOption.setCartKey(cartKey);
            cartOption.setOptionKey(optionKey);
            cartOption.setOptionPrice(option.getOptionPrice());
            cartOption.setOptionName(option.getOptionName());

            cartMapper.insertCartOption(cartOption);
        }
    }

    @Override
    public void deleteCartItem(int cartKey) {
        cartMapper.deleteCartItem(cartKey);
    }

    @Override
    public ArrayList<Cart> getCartItemsByStoreInfoKey(int userKey, int storeInfoKey) {
        Map<String, Integer> map = new HashMap<>();
        map.put("userKey", userKey);
        map.put("storeInfoKey", storeInfoKey);
        return cartMapper.findByStoreInfoKey(map);
    }
}
