package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.service.UserCartService;
import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;
import kr.co.jhta.app.delideli.user.coupon.service.UserCouponService;
import kr.co.jhta.app.delideli.user.dto.CartItemDTO;
import kr.co.jhta.app.delideli.user.dto.OrderRequestDTO;
import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.domain.OrderDetail;
import kr.co.jhta.app.delideli.user.order.service.UserOrderService;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserOrderController {

    @Autowired
    private final UserCartService userCartService;
    @Autowired
    private final UserStoreService userStoreService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserCouponService userCouponService;
    @Autowired
    private final UserOrderService userOrderService;

    @GetMapping("/order")
    public String orderPage(@RequestParam("storeInfoKey") int storeInfoKey, @AuthenticationPrincipal User user, Model model) {

        // 가게 정보 가져오기
        StoreInfo storeInfo = userStoreService.getStoreInfoById(storeInfoKey);
        UserAccount userAccount = userService.findUserById(user.getUsername());
        ArrayList<UserAddress> addressList = userService.userAddressList(userAccount.getUserKey());

        // 장바구니 아이템 가져오기
        ArrayList<Cart> cartItems = userCartService.getCartItemsByStoreInfoKey(userAccount.getUserKey(), storeInfoKey);

        // 쿠폰 목록 가져오기
        ArrayList<Coupon> coupons = userCouponService.getCouponsByUserKey(userAccount.getUserKey());

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

    @PostMapping("/confirmOrder")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmOrder(@RequestBody OrderRequestDTO orderRequestDTO, @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserAccount userAccount = userService.findUserById(user.getUsername());

            // 주문 정보 저장
            Order order = new Order();
            order.setUserKey(userAccount.getUserKey());
            order.setStoreInfoKey(orderRequestDTO.getStoreInfoKey());
            order.setOrderNo(orderRequestDTO.getOrderNo());
            order.setAddress(orderRequestDTO.getAddress());
            order.setOrderMethod(orderRequestDTO.getOrderMethod());
            order.setUorderPrice(orderRequestDTO.getTotalPayment());
            order.setUorderPayment(orderRequestDTO.getPaymentMethod());
            order.setRiderDesc(orderRequestDTO.getRiderDesc());
            order.setShopDesc(orderRequestDTO.getShopDesc());
            userOrderService.saveOrder(order);

            // 주문 상세 정보 저장
            for (CartItemDTO cartItem : orderRequestDTO.getCartItems()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderKey(order.getOrderKey());
                orderDetail.setMenuName(cartItem.getMenuName());
                orderDetail.setOptionName(cartItem.getOptionName());
                orderDetail.setQuantity(cartItem.getQuantity());
                userOrderService.saveOrderDetail(orderDetail);

                userCartService.removeCartItem(cartItem.getCartKey());
            }

            // 쿠폰 사용 처리
            if (orderRequestDTO.getCouponKey() != null) {
                userCouponService.removeCoupon(orderRequestDTO.getCouponKey(), userAccount.getUserKey());
            }

            // 포인트 결제 처리
            if ("포인트 결제".equals(orderRequestDTO.getPaymentMethod())) {
                userService.updateUserPoint(userAccount.getUserKey(), userAccount.getUserPoint() - orderRequestDTO.getTotalPayment());
            }

            response.put("success", true);
            response.put("message", "주문이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            //log.error("주문 처리 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "주문 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/myOrder")
    public String myOrder(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        model.addAttribute("active", "myOrder");

        // 사용자의 모든 주문을 가져옴
        ArrayList<Order> userOrders = userOrderService.getOrdersByUserKey(userAccount.getUserKey());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 (E)");

        ArrayList<Order> inProgressOrders = new ArrayList<>();
        ArrayList<Order> completedOrders = new ArrayList<>();

        // 각 주문에 대한 상세 정보를 가져옴
        for (Order order : userOrders) {
            ArrayList<OrderDetail> orderDetails = userOrderService.getOrderDetailsByOrderKey(order.getOrderKey());
            order.setOrderDetails(orderDetails);

            StoreInfo storeInfo = userStoreService.getStoreInfoById(order.getStoreInfoKey());
            order.setStoreName(storeInfo.getStoreName());

            String formattedDate = order.getOrderRegdate().format(formatter);
            order.setFormattedOrderDate(formattedDate);


            // 주문 상태 메시지 설정
            String statusMessage = "";

            switch (order.getOrderMethod()) {
                case "배달":
                case "포장":
                    statusMessage = "가게 접수중";
                    break;
                case "배달(조리중)", "포장(조리중)":
                    // 예상 도착 시간을 계산합니다.
                    LocalDateTime estimatedArrivalTime = order.getOrderUpdate().plusMinutes(Long.parseLong(order.getOrderEstimatedTime()));

                    // 시와 분만 추출하여 형식화합니다.
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = estimatedArrivalTime.format(timeFormatter);

                    statusMessage = "조리중 - " + formattedTime + " 도착 예정";
                    break;
                case "배달중":
                    statusMessage = "배달중";
                    break;
                case "포장(픽업대기)":
                    statusMessage = "포장(픽업대기)";
                    break;
                case "포장 완료", "배달 완료":
                    statusMessage = "주문 완료";
                    break;
                case "취소됨":
                    statusMessage = "주문 취소됨";
                    break;
                case "거절됨":
                    statusMessage = "주문 거절됨";
                    break;
            }

            order.setStatusMessage(statusMessage);

            //log.info("order  >>>>>>>>>>>>>>>>>> {}", order);
            
            // 주문 상태에 따라 리스트에 추가
            if (order.getOrderMethod().equals("배달") || order.getOrderMethod().equals("포장") || order.getOrderMethod().equals("배달(조리중)") || order.getOrderMethod().equals("포장(조리중)") || order.getOrderMethod().equals("배달중") || order.getOrderMethod().equals("포장(픽업대기)")) {
                inProgressOrders.add(order);
            } else {
                completedOrders.add(order);
            }
        }

        model.addAttribute("inProgressOrders", inProgressOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("orders", userOrders);

        return "user/mypage/myOrder";
    }

    @PostMapping("/cancelOrder")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@RequestBody Map<String, Integer> payload) {
        try {
            int orderKey = payload.get("orderKey");

            boolean success = userOrderService.cancelOrder(orderKey);

            if (success) {
                return ResponseEntity.ok("주문이 성공적으로 취소되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문 취소에 실패했습니다.");
            }
        } catch (Exception e) {
            // 로그에 오류 메시지 기록
            e.printStackTrace();  // 서버 로그에 오류 내용을 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/orderDetail/{orderKey}")
    public String orderDetail(@PathVariable("orderKey") int orderKey, @AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);

        Order order = userOrderService.getOrderByKey(orderKey);
        StoreInfo storeInfo = userStoreService.getStoreInfoById(order.getStoreInfoKey());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 mm분");
        String formattedDate = order.getOrderRegdate().format(formatter);
        order.setFormattedOrderDate(formattedDate);

        // 주문 상태 메시지 설정
        String statusMessage = "";

        switch (order.getOrderMethod()) {
            case "배달":
            case "포장":
                statusMessage = "가게 접수중";
                break;
            case "배달(조리중)", "포장(조리중)":
                statusMessage = "조리중";
                break;
            case "배달중":
                statusMessage = "배달중";
                break;
            case "포장(픽업대기)":
                statusMessage = "포장(픽업대기)";
                break;
            case "완료":
                statusMessage = "주문 완료";
                break;
            case "취소됨":
                statusMessage = "주문 취소됨";
                break;
            case "거절됨":
                statusMessage = "주문 거절됨";
                break;
        }

        order.setStatusMessage(statusMessage);

        //log.info("order" + order);
        //log.info("storeInfo" + storeInfo);
        model.addAttribute("active", "myOrder");
        model.addAttribute("order", order);
        model.addAttribute("store", storeInfo);
        return "user/order/orderDetail";
    }
}