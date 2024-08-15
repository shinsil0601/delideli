package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.service.CartService;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/user/order")
    public String orderPage(@RequestParam("storeInfoKey") int storeInfoKey, Model model) {
        // storeInfoKey에 해당하는 가게 정보를 가져옵니다.
        StoreInfo storeInfo = storeService.getStoreInfoById(storeInfoKey);

        // storeInfoKey에 해당하는 장바구니 아이템들을 가져옵니다.
        ArrayList<Cart> cartItems = cartService.getCartItemsByStoreInfoKey(storeInfoKey);

        // 가게 정보와 장바구니 아이템을 모델에 추가합니다.
        model.addAttribute("storeInfo", storeInfo);
        model.addAttribute("cartItems", cartItems);

        // 총 금액 계산
        int totalAmount = cartItems.stream().mapToInt(Cart::getTotalPrice).sum();
        model.addAttribute("totalAmount", totalAmount);

        // 주문 페이지로 이동
        return "user/order/order";
    }
}
