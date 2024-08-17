package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.like.domain.Like;
import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.*;
import kr.co.jhta.app.delideli.user.store.service.UserCategoryService;
import kr.co.jhta.app.delideli.user.like.service.UserLikeService;
import kr.co.jhta.app.delideli.user.store.service.UserStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserStoreController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserCategoryService userCategoryService;
    @Autowired
    private final UserStoreService userStoreService;
    @Autowired
    private final UserLikeService userLikeService;

    // 가게 목록
    @GetMapping("/category/{categoryId}")
    public String categoryPage(@PathVariable("categoryId") int categoryId,
                               @AuthenticationPrincipal User user,
                               @RequestParam(value = "address", required = false) String address,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                               Model model) {
        // 모든 카테고리 목록을 가져와서 모델에 추가
        ArrayList<Category> categoryList = userCategoryService.getAllCategory();
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
            allStores = userStoreService.getAllStoresInRegion(region);
        } else {
            // 특정 카테고리의 경우
            allStores = userStoreService.getAllStoresByCategoryAndRegion(categoryId, region);
        }

        // 각 가게에 메뉴와 리뷰 정보를 추가
        for (StoreInfo store : allStores) {
            Menu firstMenu = userStoreService.getFirstMenuForStore(store.getStoreInfoKey());
            Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());

            store.setFirstMenu(firstMenu);  // 첫 번째 메뉴 설정
            store.setAverageRating(averageRating != null ? averageRating : 0.0);  // 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정
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
    public String filterStoresByAddress(@RequestParam("address") String address, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        // 주소에서 지역 정보를 추출
        String region = extractRegion(address);

        // 해당 지역의 가게 목록을 가져옴
        ArrayList<StoreInfo> allStores = userStoreService.getAllStoresInRegion(region);

        // 각 가게에 메뉴와 리뷰 정보를 추가
        for (StoreInfo store : allStores) {
            Menu firstMenu = userStoreService.getFirstMenuForStore(store.getStoreInfoKey());
            Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());

            store.setFirstMenu(firstMenu);  // 첫 번째 메뉴 설정
            store.setAverageRating(averageRating != null ? averageRating : 0.0);  // 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정
        }

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
            allStores = userStoreService.getAllStoresInRegion(region);
        } else {
            // 검색어와 지역 정보를 기반으로 가게 목록을 가져옴
            allStores = userStoreService.searchAllStoresByNameAndRegion(query, region);
        }

        // 각 가게에 메뉴와 리뷰 정보를 추가
        for (StoreInfo store : allStores) {
            Menu firstMenu = userStoreService.getFirstMenuForStore(store.getStoreInfoKey());
            Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());

            store.setFirstMenu(firstMenu);  // 첫 번째 메뉴 설정
            store.setAverageRating(averageRating != null ? averageRating : 0.0);  // 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정
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

    // 가게 상세 정보
    @GetMapping("/storeDetail/{storeInfoKey}")
    public String storeDetailPage(@AuthenticationPrincipal User user,
                                  @PathVariable("storeInfoKey") int storeInfoKey,
                                  @RequestParam(value = "tab", defaultValue = "menu") String tab, Model model) {
        // 가게 정보 가져오기
        StoreInfo store = userStoreService.getStoreInfoById(storeInfoKey);

        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);

            // 찜 상태 확인
            Like like = userLikeService.findLikeByUserAndStore(userAccount.getUserKey(), storeInfoKey);
            model.addAttribute("isLiked", like != null && like.getLikeStatus() == 1);
        }

        Map<MenuGroup, ArrayList<Menu>> groupedMenus = userStoreService.getMenuGroupedByMenuGroup(storeInfoKey);
        ArrayList<Review> reviewList = userStoreService.getReviewListByStore(storeInfoKey);

        Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());
        store.setAverageRating(averageRating != null ? averageRating : 0.0);  // 평균 리뷰 점수 설정
        store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정

        // 모델에 데이터 추가
        model.addAttribute("store", store);
        model.addAttribute("groupedMenus", groupedMenus);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("tab", tab);

        return "user/store/storeDetail";  // 상세 페이지로 이동
    }

    // 찜하기/취소 기능
    @PostMapping("/toggleLike")
    @ResponseBody
    public String toggleLike(@RequestParam int storeInfoKey, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "not_logged_in";
        }
        UserAccount userAccount = userService.findUserById(user.getUsername());
        userLikeService.toggleLike(userAccount.getUserKey(), storeInfoKey);
        return "success";
    }

    // 메뉴 상세정보
    @GetMapping("/menuDetail/{menuKey}")
    public String getMenuDetail(@PathVariable int menuKey,
                                @AuthenticationPrincipal User user,
                                Model model) {
        // 메뉴 정보 및 옵션 그룹을 가져옴
        Menu menu = userStoreService.getMenuById(menuKey);
        ArrayList<OptionGroup> optionGroups = userStoreService.getOptionGroupsByMenuId(menuKey);

        // 사용자 정보가 null이 아니라면 로그인된 상태
        boolean isLoggedIn = (user != null);

        // 로그인된 상태일 경우, 사용자 정보를 모델에 추가
        if (isLoggedIn) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }

        // 모델에 필요한 데이터 추가
        model.addAttribute("menu", menu);
        model.addAttribute("optionGroups", optionGroups);
        model.addAttribute("isLoggedIn", isLoggedIn);  // 로그인 상태를 모델에 추가

        // 메뉴 상세 페이지로 이동
        return "user/store/menuDetail";
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
