package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.service.CartService;
import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;
import kr.co.jhta.app.delideli.user.coupon.service.CouponService;
import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.service.OrderService;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    @Autowired
    private final CartService cartService;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CouponService couponService;

    @GetMapping("/order")
    public String orderPage(@RequestParam("storeInfoKey") int storeInfoKey, @AuthenticationPrincipal User user, Model model) {

        // 가게 정보 가져오기
        StoreInfo storeInfo = storeService.getStoreInfoById(storeInfoKey);
        UserAccount userAccount = userService.findUserById(user.getUsername());
        ArrayList<UserAddress> addressList = userService.userAddressList(userAccount.getUserKey());

        // 장바구니 아이템 가져오기
        ArrayList<Cart> cartItems = cartService.getCartItemsByStoreInfoKey(userAccount.getUserKey(), storeInfoKey);

        // 쿠폰 목록 가져오기
        ArrayList<Coupon> coupons = couponService.getCouponsByUserKey(userAccount.getUserKey());

        // 모델에 데이터 추가
        model.addAttribute("user", userAccount);
        model.addAttribute("addressList", addressList);
        model.addAttribute("storeInfo", storeInfo);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("coupons", coupons);

        // 총금액 계산
        int totalAmount = 0;

        for (Cart cartItem : cartItems) {
            int totalOptionPrice = cartItem.getCartOptions().stream()
                    .map(option -> Optional.ofNullable(option.getOptionPrice()).orElse(0))
                    .mapToInt(Integer::intValue)
                    .sum();

            int totalPrice = (cartItem.getMenu().getMenuPrice() + totalOptionPrice) * cartItem.getQuantity();
            cartItem.setTotalPrice(totalPrice);

            totalAmount += totalPrice;
        }

        model.addAttribute("totalAmount", totalAmount);
        return "user/order/order";
    }
}
