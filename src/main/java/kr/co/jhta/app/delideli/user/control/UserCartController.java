package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import kr.co.jhta.app.delideli.user.cart.service.UserCartService;
import kr.co.jhta.app.delideli.user.dto.CartDTO;
import kr.co.jhta.app.delideli.user.store.domain.Menu;
import kr.co.jhta.app.delideli.user.store.domain.Option;
import kr.co.jhta.app.delideli.user.store.domain.OptionGroup;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.UserOptionService;
import kr.co.jhta.app.delideli.user.store.service.UserStoreService;
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
public class UserCartController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserStoreService userStoreService;
    @Autowired
    private final UserCartService userCartService;
    @Autowired
    private final UserOptionService userOptionService;

    // 장바구니 페이지
    @GetMapping("/myCart")
    public String myCartPage(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);

            ArrayList<Cart> cartItems = new ArrayList<>(userCartService.getCartItemsByUser(userAccount.getUserKey()));

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

    // 장바구니 추가
    @PostMapping("/addToCart")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestBody Map<String, Object> cartRequest,
                                         @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            UserAccount userAccount = userService.findUserById(user.getUsername());

            // 문자열로 받은 데이터들을 적절한 타입으로 변환
            int menuKey = Integer.parseInt(cartRequest.get("menuKey").toString());
            int quantity = Integer.parseInt(cartRequest.get("quantity").toString());

            // ArrayList로 변환
            ArrayList<Integer> selectedOptions = new ArrayList<>();
            for (Object option : (ArrayList<?>) cartRequest.get("selectedOptionKeys")) {
                selectedOptions.add(Integer.parseInt(option.toString()));
            }

            // 장바구니에 항목 추가
            userCartService.addItemToCart(userAccount.getUserKey(), menuKey, quantity, selectedOptions);

            response.put("success", true);
        } catch (Exception e) {
            //log.error("장바구니 추가 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "오류가 발생했습니다. 다시 시도해 주세요.");
        }

        return response;
    }

    // 장바구니 아이템 수정 페이지로 이동
    @GetMapping("/editCartItem/{cartKey}")
    public String editCartItemPage(@PathVariable("cartKey") int cartKey, Model model) {
        // 장바구니 아이템 정보 가져오기
        Cart cartItem = userCartService.getCartItemById(cartKey);

        // 해당 메뉴의 정보와 옵션 그룹을 가져오기
        Menu menu = userStoreService.getMenuById(cartItem.getMenuKey());
        ArrayList<OptionGroup> optionGroups = userStoreService.getOptionGroupsByMenuId(cartItem.getMenuKey());

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
            //System.out.println("Updating cart with cartKey: " + cartDTO.getCartKey());

            userCartService.updateCartItem(cartDTO.getCartKey(),
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
            userCartService.deleteCartItem(cartKey);
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

    // 같은 메뉴 담기
    @PostMapping("/addSameMenu")
    public ResponseEntity<Map<String, Object>> addSameMenu(@RequestBody Map<String, Object> requestData) {
        try {
            // 로그로 요청 데이터를 찍어봅니다.
            //log.info("Received request data: {}", requestData);

            // storeInfoKey와 userKey 추출
            int storeInfoKey = Integer.parseInt(requestData.get("storeInfoKey").toString());
            int userKey = Integer.parseInt(requestData.get("userKey").toString());


            // orderDetails는 리스트 형태로 받습니다.
            List<Map<String, Object>> orderDetails = (List<Map<String, Object>>) requestData.get("orderDetails");

            // 각 주문 항목에 대해 처리
            for (Map<String, Object> detail : orderDetails) {
                String menuName = (String) detail.get("menuName");
                int quantity = (int) detail.get("quantity");
                String optionName = (String) detail.get("optionName");

                // MenuService를 이용해 menuKey를 가져옴
                int menuKey = userStoreService.getMenuByStoreInfoKeyAndMenuName(storeInfoKey, menuName);

                // cart 테이블에 메뉴 추가
                int cartKey = userCartService.addCart(userKey, menuKey, quantity);

                // OptionService를 이용해 해당 옵션 이름에 대한 optionKey 목록을 가져옴
                ArrayList<Integer> optionKeys = userOptionService.getOptionKeysByMenuKeyAndOptionNames(menuKey, optionName);

                // 옵션 키 목록을 반복하면서 cart_options 테이블에 저장
                for (int optionKey : optionKeys) {
                    // 옵션 정보 가져오기
                    Option option = userOptionService.getOptionById(optionKey);
                    // cart_options 테이블에 데이터 추가
                    userCartService.addCartOption(cartKey, optionKey, option.getOptionPrice(), option.getOptionName());
                }

            }

            // 응답 객체를 생성합니다.
            Map<String, Object> response = Map.of("success", true, "message", "메뉴가 성공적으로 추가되었습니다.");

            // 클라이언트에 응답을 반환합니다.
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            //log.error("Error occurred while processing the request", e);
            Map<String, Object> errorResponse = Map.of("success", false, "message", "메뉴 추가에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
