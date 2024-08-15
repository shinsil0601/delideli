package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import kr.co.jhta.app.delideli.user.cart.service.CartService;
import kr.co.jhta.app.delideli.user.dto.CartDTO;
import kr.co.jhta.app.delideli.user.store.domain.Menu;
import kr.co.jhta.app.delideli.user.store.domain.OptionGroup;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final CartService cartService;

    // 장바구니 페이지
    @GetMapping("/myCart")
    public String myCartPage(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);

            ArrayList<Cart> cartItems = new ArrayList<>(cartService.getCartItemsByUser(userAccount.getUserKey()));

            Map<StoreInfo, ArrayList<Cart>> groupedItems = new HashMap<>();
            Map<StoreInfo, Integer> storeTotalPrices = new HashMap<>();

            for (Cart cartItem : cartItems) {
                int totalOptionPrice = cartItem.getCartOptions().stream()
                        .map(option -> Optional.ofNullable(option.getOptionPrice()).orElse(0))
                        .mapToInt(Integer::intValue)
                        .sum();

                // 총 가격 계산
                int totalPrice = (cartItem.getMenu().getMenuPrice() + totalOptionPrice) * cartItem.getQuantity();
                cartItem.setTotalPrice(totalPrice);

                // 가게 정보에 따라 그룹화
                StoreInfo storeInfo = cartItem.getStoreInfo();
                groupedItems.computeIfAbsent(storeInfo, k -> new ArrayList<>()).add(cartItem);

                // 가게별 총 금액 계산 및 업데이트
                storeTotalPrices.put(storeInfo, storeTotalPrices.getOrDefault(storeInfo, 0) + totalPrice);
            }

            model.addAttribute("groupedCartItems", groupedItems);
            model.addAttribute("storeTotalPrices", storeTotalPrices);
        }
        return "user/mypage/myCart";
    }


    // 장바구니 아이템 수정 페이지로 이동
    @GetMapping("/editCartItem/{cartKey}")
    public String editCartItemPage(@PathVariable("cartKey") int cartKey, Model model) {
        // 장바구니 아이템 정보 가져오기
        Cart cartItem = cartService.getCartItemById(cartKey);

        // 해당 메뉴의 정보와 옵션 그룹을 가져오기
        Menu menu = storeService.getMenuById(cartItem.getMenuKey());
        ArrayList<OptionGroup> optionGroups = storeService.getOptionGroupsByMenuId(cartItem.getMenuKey());

        // 선택된 옵션 키 가져오기
        List<Integer> selectedOptionKeys = cartItem.getCartOptions().stream()
                .map(CartOptions::getOptionKey)
                .collect(Collectors.toList());

        // 모델에 필요한 데이터 추가
        model.addAttribute("menu", menu);
        model.addAttribute("optionGroups", optionGroups);
        model.addAttribute("selectedOptionKeys", selectedOptionKeys);  // 선택된 옵션 키들
        model.addAttribute("cartItem", cartItem);

        // 장바구니 아이템 수정 페이지로 이동
        return "user/mypage/updateCartItem";
    }

    // 장바구니 아이템 수정 처리
    @PostMapping("/updateCartItem")
    public ResponseEntity<Map<String, Object>> updateCartItem(@RequestBody CartDTO cartDTO) {
        try {
            System.out.println("Updating cart with cartKey: " + cartDTO.getCartKey());

            cartService.updateCartItem(cartDTO.getCartKey(),
                    cartDTO.getQuantity(),
                    cartDTO.getSelectedOptionKeys());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 여기서 예외 메시지를 로깅합니다.
            e.printStackTrace(); // 또는 Logger를 사용할 수도 있습니다.
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage()); // 사용자에게 예외 메시지를 반환하는 대신, 로그에만 남기도록 처리할 수도 있습니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 장바구니 아이템 삭제 처리
    @PostMapping("/deleteCartItem")
    public ResponseEntity<Map<String, Object>> deleteCartItem(@RequestBody Map<String, Integer> request) {
        int cartKey = request.get("cartKey");

        try {
            cartService.deleteCartItem(cartKey);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
