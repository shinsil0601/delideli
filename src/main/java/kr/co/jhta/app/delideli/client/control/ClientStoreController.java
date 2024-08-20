package kr.co.jhta.app.delideli.client.control;


import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.service.ClientCategoryService;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
import kr.co.jhta.app.delideli.common.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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

        return "client/store/store.register";
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

    @GetMapping("/storeList")
    public String storeList(@AuthenticationPrincipal User user,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "storeAccess", required = false) String storeAccess,
                            @RequestParam(value = "businessStatus", required = false) String businessStatus,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                            Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());

        // 필터와 검색 조건을 전달하여 페이징된 결과를 가져옴
        List<ClientStoreInfo> storeList = clientStoreService.filterStoresWithPaging(
                clientAccount.getClientKey(), storeAccess, businessStatus, keyword, page, pageSize);

        // 각 가게의 영업 상태 계산
        for (ClientStoreInfo store : storeList) {
            store.calculateBusinessStatus();
        }

        // 총 가게 수 계산
        int totalStores = clientStoreService.getTotalStores(clientAccount.getClientKey(), storeAccess, businessStatus, keyword);
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeList);
        model.addAttribute("storeAccess", storeAccess);
        model.addAttribute("businessStatus", businessStatus);
        model.addAttribute("keyword", keyword);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("on", "list");

        return "client/store/store.list";
    }

    // 일시정지 활성화/비활성화
    @PostMapping("/toggleStorePause/{storeInfoKey}")
    @ResponseBody
    public Map<String, Object> toggleStorePause(@PathVariable("storeInfoKey") int storeInfoKey, @RequestBody Map<String, Boolean> requestData) {
        boolean newPauseStatus = requestData.get("storePause");

        // storePause 상태 업데이트
        clientStoreService.updateStorePause(storeInfoKey, newPauseStatus);

        // 성공 여부와 새로운 상태를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newStatus", newPauseStatus);

        return response;
    }

    //가게 영업 일시정지버튼 DB값 변경
    @PostMapping("/storePause/{storeInfoKey}")
    @ResponseBody
    public String toggleStorePause(@PathVariable("storeInfoKey") int storeInfoKey) {
        // 현재 store_pause 값을 가져옴
        boolean currentPauseState = clientStoreService.getStorePauseState(storeInfoKey);

        // 상태를 반전시킴
        boolean newPauseState = !currentPauseState;

        // 상태를 업데이트
        clientStoreService.updateStorePause(storeInfoKey, newPauseState);

        // 새 상태를 클라이언트에 반환
        return newPauseState ? "일시정지" : "영업중";
    }

    // 가게 제거
    @PostMapping("/toggleStoreDelete/{storeInfoKey}")
    @ResponseBody
    public Map<String, Object> toggleStoreDelete(@PathVariable("storeInfoKey") int storeInfoKey) {
        // 현재 store_delete 값을 가져옴
        boolean currentDeleteState = clientStoreService.getStoreDeleteState(storeInfoKey);

        // 상태를 반전시킴
        boolean newDeleteState = !currentDeleteState;

        // 상태를 업데이트
        clientStoreService.updateStoreDelete(storeInfoKey, newDeleteState);

        // 성공 여부와 새로운 상태를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newStatus", newDeleteState);

        return response;
    }

    // 가게 수정창으로 이동
    @GetMapping("/storeEdit/{storeInfoKey}")
    public String editStore(@PathVariable("storeInfoKey") int storeInfoKey, Model model) {
        ClientStoreInfo store = clientStoreService.getStoreDetail(storeInfoKey);
        store.formatTimes();

        ArrayList<ClientCategory> category = clientCategoryService.getAllCategory();
        List<ClientCategory> storeCategories = clientStoreService.getStoreCategories(storeInfoKey);

        model.addAttribute("store", store);
        model.addAttribute("categories", category);
        model.addAttribute("storeCategories", storeCategories);

        return "client/store/store.edit";
    }

    @PostMapping("/storeEdit/{storeInfoKey}")
    public String updateStore(@PathVariable("storeInfoKey") int storeInfoKey, @RequestParam("storeName") String storeName, @RequestParam(value = "categories", required = false) String[] categories, @RequestParam("newAddress") String storeAddress, @RequestParam("storeZipcode") String storeZipcode, @RequestParam("storeAddrDetail") String storeAddrDetail, @RequestParam("storePhone") String storePhone, @RequestParam("minOrderAmount") int minOrderAmount, @RequestParam("orderAmount1") int orderAmount1, @RequestParam("deliveryAmount1") int deliveryAmount1, @RequestParam(value = "orderAmount2", required = false) Integer orderAmount2, @RequestParam(value = "deliveryAmount2", required = false) Integer deliveryAmount2, @RequestParam(value = "orderAmount3", required = false) Integer orderAmount3, @RequestParam(value = "deliveryAmount3", required = false) Integer deliveryAmount3, @RequestParam("openTime") String openTime, @RequestParam("closeTime") String closeTime, @RequestParam("storeDetailInfo") String storeDetailInfo, @RequestParam("storeOriginInfo") String storeOriginInfo, @RequestParam(value = "storeBusinessRegistrationFile", required = false) MultipartFile storeBusinessRegistrationFile, @RequestParam(value = "storeBusinessReportFile", required = false) MultipartFile storeBusinessReportFile, @RequestParam(value = "storeProfileImg", required = false) MultipartFile storeProfileImg, @RequestParam("currentStoreBusinessRegistrationFile") String currentStoreBusinessRegistrationFile, @RequestParam("currentStoreBusinessReportFile") String currentStoreBusinessReportFile, @RequestParam("currentStoreProfileImg") String currentStoreProfileImg, RedirectAttributes redirectAttributes) {

        // 파일 저장 처리
        String regFilePath = currentStoreBusinessRegistrationFile;
        String reportFilePath = currentStoreBusinessReportFile;
        String profileImgPath = currentStoreProfileImg;

        if (storeBusinessRegistrationFile != null && !storeBusinessRegistrationFile.isEmpty()) {
            regFilePath = saveProfileImage(storeBusinessRegistrationFile);
        }

        if (storeBusinessReportFile != null && !storeBusinessReportFile.isEmpty()) {
            reportFilePath = saveProfileImage(storeBusinessReportFile);
        }

        if (storeProfileImg != null && !storeProfileImg.isEmpty()) {
            profileImgPath = saveProfileImage(storeProfileImg);
        }

        // 가게 정보를 서비스에 전달하여 업데이트
        clientStoreService.updateStore(storeInfoKey, storeName, categories, storeAddress, storeZipcode, storeAddrDetail, storePhone,
                minOrderAmount, orderAmount1, deliveryAmount1, orderAmount2, deliveryAmount2, orderAmount3, deliveryAmount3,
                openTime, closeTime, storeDetailInfo, storeOriginInfo, regFilePath, reportFilePath, profileImgPath);

        redirectAttributes.addFlashAttribute("message", "가게 정보가 성공적으로 수정되었습니다.");
        return "redirect:/client/storeList";
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

        return "client/images/uploads/" + uniqueFilename;
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
