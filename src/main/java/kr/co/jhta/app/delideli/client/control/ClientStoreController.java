package kr.co.jhta.app.delideli.client.control;


import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.service.ClientCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientStoreController {

    @Autowired
    private final ClientService clientService;
    @Autowired
    private final ClientCategoryService clientCategoryService;

    @GetMapping("/storeRegister")
    public String storeRegister(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientCategory> category = clientCategoryService.getAllCategory();
        model.addAttribute("client", clientAccount);
        model.addAttribute("category", category);

        return "/client/store/store.register";
    }

    @PostMapping("/storeRegister")
    public String registerStore(
            @AuthenticationPrincipal User user,
            @RequestParam("store-name") String storeName,
            @RequestParam("categories") String[] categories,
            @RequestParam("newAddress") String address,
            @RequestParam("newZipcode") String zipcode,
            @RequestParam("newAddrDetail") String addrDetail,
            @RequestParam("store-phone") String storePhone,
            @RequestParam("min-amount") String minAmount,
            @RequestParam("orderAmount1") String orderAmount1,
            @RequestParam("deliveryFee1") String deliveryFee1,
            @RequestParam("orderAmount2") String orderAmount2,
            @RequestParam("deliveryFee2") String deliveryFee2,
            @RequestParam("deliveryFee3") String deliveryFee3,
            @RequestParam("openTime") String openTime,
            @RequestParam("closeTime") String closeTime,
            @RequestParam("storeInfo") String storeInfo,
            @RequestParam("originInfo") String originInfo
    ) {
        // 로그로 입력된 데이터 출력
        log.info("Store Name: {}", storeName);
        log.info("Categories: {}", (Object) categories);
        log.info("Address: {}, Zipcode: {}, Detail: {}", address, zipcode, addrDetail);
        log.info("Phone: {}", storePhone);
        log.info("Min Order Amount: {}", minAmount);
        log.info("Order Amount 1: {}, Delivery Fee 1: {}", orderAmount1, deliveryFee1);
        log.info("Order Amount 2: {}, Delivery Fee 2: {}", orderAmount2, deliveryFee2);
        log.info("Delivery Fee 3: {}", deliveryFee3);
        log.info("Open Time: {}", openTime);
        log.info("Close Time: {}", closeTime);
        log.info("Store Info: {}", storeInfo);
        log.info("Origin Info: {}", originInfo);

        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ClientStoreInfo store = new ClientStoreInfo();
        store.setClientKey(clientAccount.getClientKey());
        store.setStoreName(storeName);
        store.setStoreAddress(address);
        store.setStoreZipcode(zipcode);
        store.setStoreAddrDetail(addrDetail);
        store.setStorePhone(storePhone);
        store.setMinOrderAmount(Integer.parseInt(minAmount));
        store.setOrderAmount1(Integer.parseInt(orderAmount1));
        store.setOrderAmount2(Integer.parseInt(orderAmount2));
        store.setDeliveryAmount1(Integer.parseInt(deliveryFee1));
        store.setDeliveryAmount2(Integer.parseInt(deliveryFee2));
        store.setOrderAmount3(Integer.parseInt(deliveryFee3));
        store.setOpenTime(LocalDateTime.parse(openTime));
        store.setCloseTime(LocalDateTime.parse(closeTime));
        store.setStoreDetailInfo(storeInfo);


        // 로그 출력 후 결과 페이지로 이동 (redirect 또는 다른 페이지로 이동 가능)
        return "test";
    }
}
