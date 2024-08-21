package kr.co.jhta.app.delideli.user.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // 가게 목록
    @GetMapping("/category/{categoryId}")
    public String categoryPage(@PathVariable("categoryId") int categoryId,
                               @AuthenticationPrincipal User user,
                               @RequestParam(value = "address", required = false) String address,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                               Model model, HttpServletResponse response) {

        // 모든 카테고리 목록을 가져와서 모델에 추가
        ArrayList<Category> categoryList = userCategoryService.getAllCategory();
        model.addAttribute("active", categoryId);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("currentPage", page);  // 현재 페이지 번호를 모델에 추가
        model.addAttribute("categoryId", categoryId);  // 현재 카테고리 ID를 모델에 추가
        model.addAttribute("pageSize", pageSize);  // 페이지 크기를 모델에 추가

        String region = null;
        ArrayList<StoreInfo> allStores;

        // 로그인된 사용자의 정보를 모델에 추가
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
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
            store.setAverageRating(averageRating != null ? String.format("%.1f", averageRating) : "0.0");  // 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정

            if (store.getOpenTime() != null) {
                store.setOpenTime(LocalTime.parse(store.getOpenTime()).format(timeFormatter));
            }
            if (store.getCloseTime() != null) {
                store.setCloseTime(LocalTime.parse(store.getCloseTime()).format(timeFormatter));
            }
            store.setBusinessStatus(store.getBusinessStatus());
        }

        int totalStores = allStores.size();
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지 범위 내의 데이터를 잘라서 가져옴
        ArrayList<StoreInfo> storeList = paginateStores(allStores, page, pageSize);

        // 페이지네이션 정보를 담을 map 객체 생성
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        // 가게 목록 및 페이지네이션 정보를 모델에 추가
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("map", paginationMap);
        model.addAttribute("type", "category/" + categoryId); // 카테고리 유형 전달

        return "user/store/store";
    }


    // 지역별 가게목록 추출
    @GetMapping("/filterStoresByAddress")
    public String filterStoresByAddress(@RequestParam("address") String address, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId, @AuthenticationPrincipal User user, Model model, HttpServletResponse response) {
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }
        // 주소에서 지역 정보를 추출
        String region = extractRegion(address);


        // 해당 지역의 가게 목록을 가져옴
        ArrayList<StoreInfo> allStores;
        if (categoryId == 0) {
            allStores = userStoreService.getAllStoresInRegion(region);
        } else {
            allStores = userStoreService.getAllStoresByCategoryAndRegion(categoryId, region);
        }

        // 각 가게에 메뉴와 리뷰 정보를 추가
        for (StoreInfo store : allStores) {
            Menu firstMenu = userStoreService.getFirstMenuForStore(store.getStoreInfoKey());
            Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());

            store.setFirstMenu(firstMenu);  // 첫 번째 메뉴 설정
            store.setAverageRating(averageRating != null ? String.format("%.1f", averageRating) : "0.0");  // 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정

            if (store.getOpenTime() != null) {
                store.setOpenTime(LocalTime.parse(store.getOpenTime()).format(timeFormatter));
            }
            if (store.getCloseTime() != null) {
                store.setCloseTime(LocalTime.parse(store.getCloseTime()).format(timeFormatter));
            }
            store.setBusinessStatus(store.getBusinessStatus());
        }

        int totalStores = allStores.size();
        int totalPages = (int) Math.ceil((double) totalStores / 8);

        // 페이지에 따른 가게 목록을 가져옴
        ArrayList<StoreInfo> storeList = paginateStores(allStores, 1, 8);

        // 페이지네이션 정보를 담을 map 객체 생성
        Map<String, Object> paginationMap = createPaginationMap(1, totalPages);

        // 가게 목록 및 기본 정보를 모델에 추가
        model.addAttribute("storeList", storeList);
        model.addAttribute("currentPage", 1);
        model.addAttribute("categoryId", 0); // ALL 카테고리로 가정
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("map", paginationMap);
        model.addAttribute("type", "filterStoresByAddress"); // 필터 유형 전달

        return "user/store/store";
    }

    // 가게명으로 주소 검색
    @GetMapping("/search/{categoryId}")
    public String searchStores(@PathVariable("categoryId") int categoryId,
                               @RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "address", required = false) String address,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                               @AuthenticationPrincipal User user,
                               Model model, HttpServletResponse response) {

        // 로그인된 사용자의 정보를 모델에 추가
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
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
            allStores = (categoryId == 0)
                    ? userStoreService.getAllStoresInRegion(region)
                    : userStoreService.getAllStoresByCategoryAndRegion(categoryId, region);
        } else {
            // 검색어와 지역 정보를 기반으로 가게 목록을 가져옴
            allStores = userStoreService.searchAllStoresByNameAndRegionAndCategory(categoryId, query, region);
        }

        // 검색 결과가 없는 경우의 처리
        if (allStores.isEmpty()) {
            model.addAttribute("active", categoryId);
            model.addAttribute("storeList", new ArrayList<StoreInfo>());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("selectedAddress", address);
            model.addAttribute("message", "검색 결과가 없습니다.");
            return "user/store/store";
        }

        // 각 가게에 메뉴와 리뷰 정보를 추가
        for (StoreInfo store : allStores) {
            Menu firstMenu = userStoreService.getFirstMenuForStore(store.getStoreInfoKey());
            Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());

            store.setFirstMenu(firstMenu);  // 첫 번째 메뉴 설정
            store.setAverageRating(averageRating != null ? String.format("%.1f", averageRating) : "0.0");// 평균 리뷰 점수 설정
            store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정

            if (store.getOpenTime() != null) {
                store.setOpenTime(LocalTime.parse(store.getOpenTime()).format(timeFormatter));
            }
            if (store.getCloseTime() != null) {
                store.setCloseTime(LocalTime.parse(store.getCloseTime()).format(timeFormatter));
            }
            store.setBusinessStatus(store.getBusinessStatus());
        }

        int totalStores = allStores.size();  // 검색 결과의 총 가게 수
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);  // 검색 결과를 기반으로 총 페이지 수 계산

        // 현재 페이지가 총 페이지 수보다 클 경우 총 페이지 수로 설정
        if (page > totalPages) {
            page = totalPages;
        }

        // 페이지 범위 내의 데이터를 잘라서 가져옴
        ArrayList<StoreInfo> storeList = paginateStores(allStores, page, pageSize);

        // 페이지네이션 정보를 담을 map 객체 생성
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        // 가게 목록 및 페이지네이션 정보를 모델에 추가
        model.addAttribute("active", categoryId);
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("map", paginationMap);
        model.addAttribute("type", "search");
        model.addAttribute("query", query);
        model.addAttribute("selectedAddress", address);

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
                                  @RequestParam(value = "tab", defaultValue = "menu") String tab, Model model, HttpServletResponse response) {
        // 가게 정보 가져오기
        StoreInfo store = userStoreService.getStoreInfoById(storeInfoKey);

        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);

                // 찜 상태 확인
                Like like = userLikeService.findLikeByUserAndStore(userAccount.getUserKey(), storeInfoKey);
                model.addAttribute("isLiked", like != null && like.getLikeStatus() == 1);
            }
        }

        Map<MenuGroup, ArrayList<Menu>> groupedMenus = userStoreService.getMenuGroupedByMenuGroup(storeInfoKey);
        ArrayList<Review> reviewList = userStoreService.getReviewListByStore(storeInfoKey);

        Double averageRating = userStoreService.getAverageRatingForStore(store.getStoreInfoKey());
        store.setAverageRating(averageRating != null ? String.format("%.1f", averageRating) : "0.0");  // 평균 리뷰 점수 설정
        store.setReviewCount(userStoreService.getReviewCountForStore(store.getStoreInfoKey()));  // 리뷰 개수 설정

        if (store.getOpenTime() != null) {
            store.setOpenTime(LocalTime.parse(store.getOpenTime()).format(timeFormatter));
        }
        if (store.getCloseTime() != null) {
            store.setCloseTime(LocalTime.parse(store.getCloseTime()).format(timeFormatter));
        }
        store.setBusinessStatus(store.getBusinessStatus());

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
                                Model model, HttpServletResponse response) {
        // 메뉴 정보 및 옵션 그룹을 가져옴
        Menu menu = userStoreService.getMenuById(menuKey);
        ArrayList<OptionGroup> optionGroups = userStoreService.getOptionGroupsByMenuId(menuKey);
        StoreInfo store = userStoreService.getStoreInfoById(menu.getStoreInfoKey());
        // 사용자 정보가 null이 아니라면 로그인된 상태
        boolean isLoggedIn = (user != null);

        // 로그인된 상태일 경우, 사용자 정보를 모델에 추가
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }

        // 모델에 필요한 데이터 추가
        model.addAttribute("store", store);
        model.addAttribute("menu", menu);
        model.addAttribute("optionGroups", optionGroups);
        model.addAttribute("isLoggedIn", isLoggedIn);  // 로그인 상태를 모델에 추가

        // 메뉴 상세 페이지로 이동
        return "user/store/menuDetail";
    }

    // 페이지네이션 처리 메서드
    private ArrayList<StoreInfo> paginateStores(ArrayList<StoreInfo> allStores, int page, int pageSize) {
        int totalStores = allStores.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allStores.size());

        if (fromIndex < 0 || fromIndex >= totalStores) {
            return new ArrayList<>();
        }

        return new ArrayList<>(allStores.subList(fromIndex, toIndex));
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
