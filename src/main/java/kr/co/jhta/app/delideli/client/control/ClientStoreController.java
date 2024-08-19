package kr.co.jhta.app.delideli.client.control;


import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.service.ClientCategoryService;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientStoreController {

    @Autowired
    private final ClientService clientService;
    @Autowired
    private final ClientCategoryService clientCategoryService;
    @Autowired
    private final ClientStoreService clientStoreService;

    @GetMapping("/storeRegister")
    public String storeRegister(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientCategory> category = clientCategoryService.getAllCategory();
        model.addAttribute("client", clientAccount);
        model.addAttribute("category", category);
        model.addAttribute("on", "list");

        return "/client/store/store.register";
    }

    @PostMapping("/storeRegister")
    public ResponseEntity<Map<String, Object>> registerStore(@AuthenticationPrincipal User user, @RequestParam("store-name") String storeName, @RequestParam("categories") String[] categories, @RequestParam("newAddress") String address, @RequestParam("newZipcode") String zipcode, @RequestParam("newAddrDetail") String addrDetail, @RequestParam("store-phone") String storePhone, @RequestParam("min-amount") String minAmount, @RequestParam("orderAmount1") String orderAmount1, @RequestParam("deliveryFee1") String deliveryFee1, @RequestParam("orderAmount2") String orderAmount2, @RequestParam("deliveryFee2") String deliveryFee2, @RequestParam("deliveryFee3") String deliveryFee3, @RequestParam("openTime") String openTime, @RequestParam("closeTime") String closeTime, @RequestParam("storeInfo") String storeInfo, @RequestParam("originInfo") String originInfo, @RequestParam("businessLicense") MultipartFile businessLicense, @RequestParam("operatingPermit") MultipartFile operatingPermit, @RequestParam("storeProfile") MultipartFile storeProfile) {

        Map<String, Object> response = new HashMap<>();

        try {
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
            store.setDeliveryAmount3(Integer.parseInt(deliveryFee3));
            store.setOpenTime(openTime);
            store.setCloseTime(closeTime);
            store.setStoreDetailInfo(storeInfo);
            store.setStoreOriginInfo(originInfo);

            // 사진 파일 저장 처리
            if (!businessLicense.isEmpty()) {
                String fileName1 = saveProfileImage(businessLicense);
                store.setStoreBusinessRegistrationFile(fileName1);
            }
            if (!operatingPermit.isEmpty()) {
                String fileName2 = saveProfileImage(operatingPermit);
                store.setStoreBusinessReportFile(fileName2);
            }
            if (!storeProfile.isEmpty()) {
                String fileName3 = saveProfileImage(storeProfile);
                store.setStoreProfileImg(fileName3);
            }

            // Store 저장 후 key 반환
            int storeInfoKey = clientStoreService.insertStore(store);

            // categories 배열을 반복하면서 store_category 테이블에 삽입
            for (String categoryKey : categories) {
                clientStoreService.insertStoreCategory(storeInfoKey, Integer.parseInt(categoryKey));
            }

            response.put("success", true);
            response.put("message", "등록 신청되었습니다.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "등록 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(response);
    }


    // 프로필 이미지 저장
    private String saveProfileImage(MultipartFile file) {
        String uploadDir = "src/main/resources/static/client/images/uploads/";
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        String filePath = uploadDir + uniqueFilename;

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/client/images/uploads/" + uniqueFilename;
    }
}
