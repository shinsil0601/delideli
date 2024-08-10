package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.store.domain.Category;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.CategoryService;
import kr.co.jhta.app.delideli.user.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final StoreService storeService;

    // 가게 목록
    @GetMapping("/category/{categoryId}")
    public String categoryPage(@PathVariable("categoryId") int categoryId,
                               @AuthenticationPrincipal User user,
                               @RequestParam(value = "address", required = false) String address,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                               Model model) {
        // 모든 카테고리 목록을 가져와서 모델에 추가
        ArrayList<Category> categoryList = categoryService.getAllCategory();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("currentPage", page);  // 현재 페이지 번호를 모델에 추가
        model.addAttribute("categoryId", categoryId);  // 현재 카테고리 ID를 모델에 추가
        model.addAttribute("pageSize", pageSize);  // 페이지 크기를 모델에 추가

        String region = null;
        ArrayList<StoreInfo> allStores;
        int totalStores;

        // 로그인된 사용자의 정보를 모델에 추가
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            ArrayList<UserAddress> addressList = userService.userAddressList(userAccount.getUserKey());
            model.addAttribute("user", userAccount);
            model.addAttribute("addressList", addressList);

            // 로그인한 사용자의 기본 주소를 설정
            if (address == null || address.isEmpty()) {
                for (UserAddress userAddress : addressList) {
                    if (userAddress.isDefaultAddress()) {
                        address = userAddress.getAddress() + " " + userAddress.getAddrDetail();
                        break;
                    }
                }
            }
        }

        // 사용자가 주소를 제공한 경우 해당 주소를 지역 정보로 변환
        if (address != null && !address.isEmpty()) {
            region = extractRegion(address);
            model.addAttribute("selectedAddress", address);  // 선택된 주소를 모델에 추가
        }

        // 카테고리와 지역에 따라 가게 목록을 필터링하여 가져옴
        if (categoryId == 0) {
            // ALL 카테고리의 경우
            allStores = storeService.getAllStoresInRegion(region);
        } else {
            // 특정 카테고리의 경우
            allStores = storeService.getAllStoresByCategoryAndRegion(categoryId, region);
        }

        totalStores = allStores.size();
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지 범위 내의 데이터를 잘라서 가져옴
        ArrayList<StoreInfo> storeList = paginateStores(allStores, page, pageSize);

        // 가게 목록 및 총 페이지 수를 모델에 추가
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPages", totalPages);

        // user/store/store.html 페이지를 반환
        return "user/store/store";
    }

    // 지역별 가게목록 추출
    @GetMapping("/filterStoresByAddress")
    public String filterStoresByAddress(@RequestParam("address") String address, Model model) {
        // 주소에서 지역 정보를 추출
        String region = extractRegion(address);

        // 해당 지역의 가게 목록을 가져옴
        ArrayList<StoreInfo> allStores = storeService.getAllStoresInRegion(region);

        // 페이지네이션 처리
        ArrayList<StoreInfo> storeList = paginateStores(allStores, 1, 8);

        // 가게 목록 및 기본 정보를 모델에 추가
        model.addAttribute("storeList", storeList);
        model.addAttribute("currentPage", 1);
        model.addAttribute("categoryId", 0); // ALL 카테고리로 가정
        model.addAttribute("totalPages", (int) Math.ceil((double) allStores.size() / 8));

        // user/store/store.html 페이지로 반환
        return "user/store/store";
    }

    // 가게명으로 주소 검색
    @GetMapping("/search")
    public String searchStores(@RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "address", required = false) String address,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                               @AuthenticationPrincipal User user,
                               Model model) {

        // 로그인된 사용자의 정보를 모델에 추가
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            ArrayList<UserAddress> addressList = userService.userAddressList(userAccount.getUserKey());
            model.addAttribute("user", userAccount);
            model.addAttribute("addressList", addressList);

            // 로그인한 사용자의 기본 주소를 설정
            if (address == null || address.isEmpty()) {
                for (UserAddress userAddress : addressList) {
                    if (userAddress.isDefaultAddress()) {
                        address = userAddress.getAddress() + " " + userAddress.getAddrDetail();
                        break;
                    }
                }
            }
        }

        // 페이지 값이 1보다 작을 경우 1로 초기화
        if (page < 1) {
            page = 1;
        }

        // 주소에서 지역 정보를 추출
        String region = extractRegion(address);

        ArrayList<StoreInfo> allStores;

        // 검색어가 없을 경우 모든 가게를 검색
        if (query == null || query.trim().isEmpty()) {
            allStores = storeService.getAllStoresInRegion(region);
        } else {
            // 검색어와 지역 정보를 기반으로 가게 목록을 가져옴
            allStores = storeService.searchAllStoresByNameAndRegion(query, region);
        }

        int totalStores = allStores.size();
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지 범위 내의 데이터를 잘라서 가져옴
        ArrayList<StoreInfo> storeList = paginateStores(allStores, page, pageSize);

        // 가게 목록 및 페이지네이션 정보를 모델에 추가
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("categoryId", 0); // 검색 시 ALL 카테고리로 가정

        // user/store/store.html 페이지를 반환
        return "user/store/store";
    }


    // 주소에서 구, 동, 읍 등의 지역 정보를 추출하는 메서드
    private String extractRegion(String address) {
        if (address == null) {
            return null;
        }

        // 주소에서 구, 동, 읍 패턴을 검색
        Pattern pattern = Pattern.compile("([가-힣]+구)|([가-힣]+동)|([가-힣]+읍)");
        Matcher matcher = pattern.matcher(address);

        // 매칭되는 지역 정보를 반환
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                return matcher.group(1);  // 구 단위
            } else if (matcher.group(2) != null) {
                return matcher.group(2);  // 동 단위
            } else if (matcher.group(3) != null) {
                return matcher.group(3);  // 읍 단위
            }
        }
        return null;  // 매칭되지 않는 경우
    }

    // 페이지네이션 처리 메서드
    private ArrayList<StoreInfo> paginateStores(ArrayList<StoreInfo> allStores, int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allStores.size());

        if (fromIndex > toIndex) {
            return new ArrayList<>();
        }

        return new ArrayList<>(allStores.subList(fromIndex, toIndex));
    }
}
