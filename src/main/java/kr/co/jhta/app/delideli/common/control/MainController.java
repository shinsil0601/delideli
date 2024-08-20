package kr.co.jhta.app.delideli.common.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.mapper.UserMapper;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.service.UserBoardService;
import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.Category;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.UserCategoryService;
import kr.co.jhta.app.delideli.user.store.service.UserStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;

@Controller
public class MainController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserCategoryService categoryService;
    @Autowired
    private final UserStoreService storeService;
    @Autowired
    private final UserBoardService userBoardService;

    public MainController(UserService userService, UserCategoryService categoryService, UserStoreService storeService, UserBoardService userBoardService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.userBoardService = userBoardService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user,
                       @PathVariable(required = false) Integer categoryId,
                       Model model,
                       HttpServletResponse response) {

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

        // 카테고리 목록 가져오기
        ArrayList<Category> categoryList = categoryService.getAllCategory();
        model.addAttribute("categoryList", categoryList);

        // categoryId가 없는 경우 첫 번째 카테고리 사용
        if (categoryId == null && !categoryList.isEmpty()) {
            categoryId = categoryList.get(0).getCategoryKey();
        }

        // 선택된 카테고리 정보 가져오기
        Category selectedCategory = categoryService.getCategoryById(categoryId);
        model.addAttribute("selectedCategory", selectedCategory);

        // 해당 카테고리의 가게 목록 가져오기
        ArrayList<StoreInfo> storeList = storeService.getStoresByCategory(categoryId, 8);
        for (StoreInfo store : storeList) {
            ArrayList<Review> reviewList = storeService.getReviewListByStore(store.getStoreInfoKey());
            Double averageRating = storeService.getAverageRatingForStore(store.getStoreInfoKey());
            store.setAverageRating(averageRating != null ? String.format("%.1f", averageRating) : "0.0");  // 평균 리뷰 점수 설정
            store.setReviewCount(storeService.getReviewCountForStore(store.getStoreInfoKey()));
        }
        model.addAttribute("storeList", storeList);

        //공지사항 목록 리스트(최대 4개)
        ArrayList<Board> noticeList = userBoardService.getBoardListIndex();
        model.addAttribute("noticeList", noticeList);

        return "index";
    }
}
