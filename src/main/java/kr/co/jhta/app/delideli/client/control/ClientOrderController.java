package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.order.service.ClientOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientOrderController {

    @Autowired
    private final ClientOrderService clientOrderService;
    @Autowired
    private final ClientService clientService;

    // 접수대기 주문 목록
    @GetMapping("/waitOrder/{storeInfoKey}")
    public String waitOrder(@AuthenticationPrincipal User user, @PathVariable("storeInfoKey") int storeInfoKey,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                            Model model) {

        // 총 주문 수를 가져옴
        int totalOrders = clientOrderService.getTotalWaitOrdersByStoreInfoKey(storeInfoKey);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        // 페이지네이션 적용된 주문 목록을 가져옴
        ArrayList<ClientOrder> waitOrderList = clientOrderService.getWaitOrdersByStoreInfoKeyWithPaging(storeInfoKey, page, pageSize);
        model.addAttribute("waitOrderList", waitOrderList);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "waitOrder/" + storeInfoKey);
        model.addAttribute("storeInfoKey", storeInfoKey);
        model.addAttribute("on", "waitOrder");

        return "client/order/waitOrder";
    }

    // 주문 상세보기 (접수 대기)
    @GetMapping("/waitOrderDetail/{orderKey}")
    public String waitOrderDetails(@AuthenticationPrincipal User user, @PathVariable("orderKey") int orderKey, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        ClientOrder waitOrderDetail = clientOrderService.getOrderDeatailByOrderKey(orderKey);
        model.addAttribute("waitOrderDetail", waitOrderDetail);
        return "client/order/waitOrderDetail";
    }

    // 주문 접수
    @PostMapping("/acceptOrder")
    @ResponseBody
    public String acceptOrder(@RequestParam("orderKey") int orderKey, @RequestParam("estimatedTime") String estimatedTime) {
        ClientOrder order = clientOrderService.getOrderDeatailByOrderKey(orderKey);

        // 배달 방식에 따라 상태를 업데이트
        if ("배달".equals(order.getOrderMethod())) {
            order.setOrderMethod("배달(조리중)");
        } else if ("포장".equals(order.getOrderMethod())) {
            order.setOrderMethod("포장(조리중)");
        }

        // 배달 예상 시간을 업데이트
        order.setOrderEstimatedTime(estimatedTime);

        // 업데이트된 주문을 저장
        clientOrderService.updateOrder(order);

        return "success";
    }

    // 주문 거절
    @PostMapping("/rejectOrder")
    @ResponseBody
    public String rejectOrder(@RequestParam("orderKey") int orderKey) {
        ClientOrder order = clientOrderService.getOrderDeatailByOrderKey(orderKey);

        // 주문 상태를 '거절됨'으로 업데이트
        order.setOrderMethod("거절됨");

        // 업데이트된 주문을 저장
        clientOrderService.updateOrder(order);

        return "success";
    }

    // 처리중 주문 목록
    @GetMapping("/processingOrder/{storeInfoKey}")
    public String processingOrder(@AuthenticationPrincipal User user, @PathVariable("storeInfoKey") int storeInfoKey,
                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                                  Model model) {

        // 총 주문 수를 가져옴
        int totalOrders = clientOrderService.getTotalprocessingOrderByStoreInfoKey(storeInfoKey);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        // 페이지네이션 적용된 주문 목록을 가져옴
        ArrayList<ClientOrder> processingOrderList = clientOrderService.getprocessingOrderByStoreInfoKeyWithPaging(storeInfoKey, page, pageSize);
        model.addAttribute("processingOrderList", processingOrderList);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "processingOrder/" + storeInfoKey);
        model.addAttribute("storeInfoKey", storeInfoKey);
        model.addAttribute("on", "processingOrder");

        return "client/order/processingOrder";
    }

    // 처리중 주문 상세보기
    @GetMapping("/OrderDetail/{orderKey}")
    public String processingOrderDetail(@AuthenticationPrincipal User user, @PathVariable("orderKey") int orderKey, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        ClientOrder processingOrderDetail = clientOrderService.getOrderDeatailByOrderKey(orderKey);
        model.addAttribute("processingOrderDetail", processingOrderDetail);
        return "client/order/OrderDetail";
    }

    // 처리중인 주문 상태 변경
    @PostMapping("/updateOrderStatus")
    @ResponseBody
    public Map<String, String> updateOrderStatus(@RequestBody Map<String, Object> request) {
        int orderKey = (int) request.get("orderKey");
        String newOrderMethod = (String) request.get("orderMethod");

        try {
            // 주문 정보 가져오기
            ClientOrder order = clientOrderService.getOrderDeatailByOrderKey(orderKey);
            // 주문 상태 업데이트
            order.setOrderMethod(newOrderMethod);
            clientOrderService.updateOrder(order);

            // 성공 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            // 실패 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            return response;
        }
    }

    // 처리완료 주문
    @GetMapping("/successOrder/{storeInfoKey}")
    public String successOrder(@AuthenticationPrincipal User user, @PathVariable("storeInfoKey") int storeInfoKey,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                               Model model) {

        // 총 주문 수를 가져옴
        int totalOrders = clientOrderService.getTotalSuccessOrdersByStoreInfoKey(storeInfoKey);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        // 페이지네이션 적용된 주문 목록을 가져옴
        ArrayList<ClientOrder> successOrderList = clientOrderService.getSuccessOrdersByStoreInfoKeyWithPaging(storeInfoKey, page, pageSize);
        model.addAttribute("successOrderList", successOrderList);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "successOrder/" + storeInfoKey);
        model.addAttribute("storeInfoKey", storeInfoKey);
        model.addAttribute("on", "successOrder");

        return "client/order/successOrder";
    }

    // 주문 조회
    @GetMapping("/searchOrder/{storeInfoKey}")
    public String searchOrder(@AuthenticationPrincipal User user, @PathVariable("storeInfoKey") int storeInfoKey,
                              @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                              Model model) {

        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);

        Map<String, Object> params = new HashMap<>();
        params.put("storeInfoKey", storeInfoKey);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        // 총 주문 수를 가져옴
        int totalOrders = clientOrderService.countTotalOrder(params);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        // 페이지네이션 적용된 주문 목록을 가져옴
        ArrayList<ClientOrder> searchOrderList = clientOrderService.getTotalOrdersByStoreInfoKeyWithPaging(params, page, pageSize);
        model.addAttribute("searchOrderList", searchOrderList);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);
        model.addAttribute("map", paginationMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "searchOrder/" + storeInfoKey);
        model.addAttribute("storeInfoKey", storeInfoKey);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("on", "searchOrder");

        return "client/order/searchOrder";
    }


    // 페이지네이션 정보를 맵핑하는 메서드
    private Map<String, Object> createPaginationMap(int currentPage, int totalPages) {
        Map<String, Object> map = new HashMap<>();
        map.put("prev", currentPage > 1);
        map.put("next", currentPage < totalPages);
        map.put("startPageNo", 1); // 필요에 따라 조정 가능
        map.put("endPageNo", totalPages); // 필요에 따라 조정 가능
        return map;
    }
}
