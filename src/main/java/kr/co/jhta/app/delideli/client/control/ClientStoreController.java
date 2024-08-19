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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

        return "client/images/uploads/" + uniqueFilename;
    }

    @GetMapping("/storeList")
    public String storeList(@AuthenticationPrincipal User user, Model model,
                            @RequestParam(value = "keytype", required = false) String keytype,
                            @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                            @RequestParam(name = "page", defaultValue = "1") int page) {

        ClientAccount clientAccount = clientService.findClientById(user.getUsername());

        ArrayList<ClientStoreInfo> storeInfo = clientStoreService.getAllStore(clientAccount.getClientKey());

        List<ClientStoreInfo> list = new ArrayList<>();;
        Map<String, Object> map = new HashMap<>();
        int totalNum;
        int recordPerPage = 5;  // 페이지당 게시물 수를 설정합니다.

        // 현재 시간을 LocalDateTime으로 정의
        LocalDateTime nowDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a h시 mm분");

        if (keyword.equals("none")) {
            // 검색어가 없을 때 총 가게 수를 계산
            totalNum = clientStoreService.getTotalStore((long) clientAccount.getClientKey());
            map = PageUtil.getPageData(totalNum, recordPerPage, page);

            int countPerPage = (int) map.get("countPerPage");
            int startNo = (int) map.get("startNo");

            // 가게 목록을 페이징에 맞게 가져옴
            list = clientStoreService.getPagedStoreList((long) clientAccount.getClientKey(), startNo, countPerPage)
                    .stream()
                    .map(store -> {
                        ClientStoreInfo dto = new ClientStoreInfo();
                        dto.setStoreName(store.getStoreName());
                        dto.setOpenTime(store.getOpenTime());
                        dto.setCloseTime(store.getCloseTime());
                        dto.setStoreAccess(store.isStoreAccess());
                        dto.setStoreInfoKey(store.getStoreInfoKey());

                        // DateTimeFormatter를 사용하여 LocalDateTime으로 변환
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime openTime = LocalDateTime.parse(store.getOpenTime(), formatter);
                        LocalDateTime closeTime = LocalDateTime.parse(store.getCloseTime(), formatter);

                        // LocalTime으로 변환
                        LocalTime openLocalTime = openTime.toLocalTime();
                        LocalTime closeLocalTime = closeTime.toLocalTime();
                        LocalTime nowLocalTime = nowDateTime.toLocalTime();

                        dto.setFormattedOpenTime(openLocalTime.format(timeFormatter));
                        dto.setFormattedCloseTime(closeLocalTime.format(timeFormatter));

                        // 영업 상태 판단
                        if (nowLocalTime.isBefore(openLocalTime) || nowLocalTime.isAfter(closeLocalTime)) {
                            dto.setBusinessStatus("영업종료");
                            dto.setStorePause(false);
                            clientStoreService.updateStorePause(store.getStoreInfoKey(), false);
                        } else {
                            if (store.isStorePause()) {
                                dto.setBusinessStatus("일시정지");
                            } else {
                                dto.setBusinessStatus("영업중");
                            }
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            log.info("none list >>>>>>>>>!!!" + list);
        } else {
            // 검색어가 있을 때 해당 검색어로 검색된 가게 수를 계산
            switch (keytype) {
                case "가게이름":
                    totalNum = clientStoreService.getTotalKeywordStore((long) clientAccount.getClientKey(), keyword);
                    map = PageUtil.getPageData(totalNum, recordPerPage, page);

                    int countPerPage = (int) map.get("countPerPage");
                    int startNo = (int) map.get("startNo");
                    log.info("keyword: ~~~~~~~~~>>>>>>>>!!!!" + keyword);

                    // 가게 이름으로 검색
                    list = clientStoreService.getPagedKeywordStoreList((long) clientAccount.getClientKey(), startNo, countPerPage, keyword)
                            .stream()
                            .map(store -> {
                                ClientStoreInfo dto = new ClientStoreInfo();
                                dto.setStoreName(store.getStoreName());
                                dto.setOpenTime(store.getOpenTime());
                                dto.setCloseTime(store.getCloseTime());
                                dto.setStoreAccess(store.isStoreAccess());
                                dto.setStoreInfoKey(store.getStoreInfoKey());

                                // DateTimeFormatter를 사용하여 LocalDateTime으로 변환
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime openTime = LocalDateTime.parse(store.getOpenTime(), formatter);
                                LocalDateTime closeTime = LocalDateTime.parse(store.getCloseTime(), formatter);

                                // LocalTime으로 변환
                                LocalTime openLocalTime = openTime.toLocalTime();
                                LocalTime closeLocalTime = closeTime.toLocalTime();
                                LocalTime nowLocalTime = nowDateTime.toLocalTime();

                                dto.setFormattedOpenTime(openLocalTime.format(timeFormatter));
                                dto.setFormattedCloseTime(closeLocalTime.format(timeFormatter));

                                // 영업 상태 판단
                                if (nowLocalTime.isBefore(openLocalTime) || nowLocalTime.isAfter(closeLocalTime)) {
                                    dto.setBusinessStatus("영업종료");
                                    dto.setStorePause(false);
                                    clientStoreService.updateStorePause(store.getStoreInfoKey(), false);
                                } else {
                                    if (store.isStorePause()) {
                                        dto.setBusinessStatus("일시정지");
                                    } else {
                                        dto.setBusinessStatus("영업중");
                                    }
                                }
                                return dto;
                            })
                            .collect(Collectors.toList());
                    break;

                case "승인여부":
                    boolean accessStatus;  // "1"이면 승인된 가게, "0"이면 미승인 가게
                    if (keyword.equals("승인")) {
                        accessStatus = true; // 승인된 가게
                    } else if (keyword.equals("미승인")) {
                        accessStatus = false; // 미승인 가게
                    } else {
                        // 잘못된 입력 처리
                        throw new IllegalArgumentException("올바른 검색어를 입력해주세요: 승인 또는 미승인");
                    }

                    totalNum = clientStoreService.getTotalByAccessStatus((long) clientAccount.getClientKey(), accessStatus);
                    map = PageUtil.getPageData(totalNum, recordPerPage, page);

                    countPerPage = (int) map.get("countPerPage");
                    startNo = (int) map.get("startNo");

                    // 승인 여부로 검색
                    list = clientStoreService.getPagedByAccessStatus((long) clientAccount.getClientKey(), startNo, countPerPage, accessStatus)
                            .stream()
                            .map(store -> {
                                ClientStoreInfo dto = new ClientStoreInfo();
                                dto.setStoreName(store.getStoreName());
                                dto.setOpenTime(store.getOpenTime());
                                dto.setCloseTime(store.getCloseTime());
                                dto.setStoreAccess(store.isStoreAccess());
                                dto.setStoreInfoKey(store.getStoreInfoKey());

                                // DateTimeFormatter를 사용하여 LocalDateTime으로 변환
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime openTime = LocalDateTime.parse(store.getOpenTime(), formatter);
                                LocalDateTime closeTime = LocalDateTime.parse(store.getCloseTime(), formatter);

                                // LocalTime으로 변환
                                LocalTime openLocalTime = openTime.toLocalTime();
                                LocalTime closeLocalTime = closeTime.toLocalTime();
                                LocalTime nowLocalTime = nowDateTime.toLocalTime();

                                dto.setFormattedOpenTime(openLocalTime.format(timeFormatter));
                                dto.setFormattedCloseTime(closeLocalTime.format(timeFormatter));

                                // 영업 상태 판단
                                if (nowLocalTime.isBefore(openLocalTime) || nowLocalTime.isAfter(closeLocalTime)) {
                                    dto.setBusinessStatus("영업종료");
                                    dto.setStorePause(false);
                                    clientStoreService.updateStorePause(store.getStoreInfoKey(), false);
                                } else {
                                    if (store.isStorePause()) {
                                        dto.setBusinessStatus("일시정지");
                                    } else {
                                        dto.setBusinessStatus("영업중");
                                    }
                                }
                                return dto;
                            })
                            .collect(Collectors.toList());
                    break;

                case "영업상태":
                    list = clientStoreService.getAllStores((long) clientAccount.getClientKey())
                            .stream()
                            .map(store -> {
                                ClientStoreInfo dto = new ClientStoreInfo();
                                dto.setStoreName(store.getStoreName());
                                dto.setOpenTime(store.getOpenTime());
                                dto.setCloseTime(store.getCloseTime());
                                dto.setStoreAccess(store.isStoreAccess());
                                dto.setStoreInfoKey(store.getStoreInfoKey());

                                // DateTimeFormatter를 사용하여 LocalDateTime으로 변환
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime openTime = LocalDateTime.parse(store.getOpenTime(), formatter);
                                LocalDateTime closeTime = LocalDateTime.parse(store.getCloseTime(), formatter);

                                // LocalTime으로 변환
                                LocalTime openLocalTime = openTime.toLocalTime();
                                LocalTime closeLocalTime = closeTime.toLocalTime();
                                LocalTime nowLocalTime = nowDateTime.toLocalTime();

                                dto.setFormattedOpenTime(openLocalTime.format(timeFormatter));
                                dto.setFormattedCloseTime(closeLocalTime.format(timeFormatter));

                                // 영업 상태 판단 및 필터링
                                if (nowLocalTime.isBefore(openLocalTime) || nowLocalTime.isAfter(closeLocalTime)) {
                                    dto.setBusinessStatus("영업종료");
                                    dto.setStorePause(false);
                                    clientStoreService.updateStorePause(store.getStoreInfoKey(), false);
                                } else if (store.isStorePause()) {
                                    dto.setBusinessStatus("일시정지");
                                } else {
                                    dto.setBusinessStatus("영업중");
                                }

                                return dto;
                            })
                            .filter(dto -> dto.getBusinessStatus().equals(keyword)) // 영업 상태로 필터링
                            .collect(Collectors.toList());

                    // 필터링된 목록으로 페이징 처리
                    totalNum = list.size();
                    map = PageUtil.getPageData(totalNum, recordPerPage, page);
                    int fromIndex = Math.min((page - 1) * recordPerPage, totalNum);
                    int toIndex = Math.min(fromIndex + recordPerPage, totalNum);
                    list = list.subList(fromIndex, toIndex);
                    break;

            }

            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeInfo);
        model.addAttribute("map", map);
        model.addAttribute("list", list);
        model.addAttribute("on", "list");

        return "client/store/store.list";
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

}
