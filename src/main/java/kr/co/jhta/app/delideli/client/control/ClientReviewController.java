package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import kr.co.jhta.app.delideli.client.review.service.ClientReviewService;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientReviewController {
    @Autowired
    private final ClientService clientService;
    @Autowired
    private ClientReviewService clientReviewService;
    @Autowired
    private final ClientStoreService clientStoreService;
    @Autowired
    private final UserService userService;

    @GetMapping("/review")
    public String clientReview(@AuthenticationPrincipal User user,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);

        // 가게 목록 페이징 처리
        ArrayList<ClientStoreInfo> storeList = clientStoreService.getAllStoreWithPaging(clientAccount.getClientKey(), page, pageSize);
        model.addAttribute("store", storeList);

        int totalStores = clientStoreService.getTotalStoreCountByClientKey(clientAccount.getClientKey());
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize); // 페이지 사이즈 설정
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "review");
        model.addAttribute("on", "review");

        return "client/review/review.list";
    }

    // 리뷰 상세보기 (페이지네이션 적용)
    @GetMapping("/review/{storeKey}")
    public String clientReview(@AuthenticationPrincipal User user,
                               @PathVariable("storeKey") String storeKey,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ClientStoreInfo store = clientStoreService.getStoreDetail(Integer.parseInt(storeKey));

        // 가게 리뷰 페이징 처리
        ArrayList<ClientReview> reviewList = clientReviewService.getAllReviewWithPaging(clientAccount.getClientKey(), storeKey, page, pageSize);
        for (ClientReview review : reviewList) {
            UserAccount userAccount = userService.getUserAccountByUserKey(review.getUserKey());
            review.setUserNickname(userAccount.getUserNickname());
        }

        int totalReviews = clientReviewService.getTotalReviewsByStoreKey(clientAccount.getClientKey(), storeKey);
        int totalPages = (int) Math.ceil((double) totalReviews / pageSize);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", store);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize); // 페이지 사이즈 설정
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "review");
        model.addAttribute("on", "review");

        return "client/review/review.view";
    }


    // 리뷰 신고 처리 (AJAX 요청)
    @PostMapping("/reviewReport/{reviewKey}")
    @ResponseBody
    public Map<String, Object> reportReview(@PathVariable("reviewKey") int reviewKey) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = clientReviewService.reportReview(reviewKey);
            if (success) {
                response.put("status", "success");
            } else {
                response.put("status", "failure");
            }
        } catch (IllegalStateException e) {
            response.put("status", "already_reported");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
        }
        return response;

    }

    // 리뷰 답변 수정
    @PostMapping("/editReview/{reviewKey}")
    @ResponseBody
    public Map<String, String> editReview(@PathVariable int reviewKey, @RequestBody Map<String, String> payload) {
        String updatedComment = payload.get("comment");

        clientReviewService.updateComment(reviewKey, updatedComment);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
    }

    // 리뷰 답변 등록
    @PostMapping("/saveReview/{reviewKey}")
    @ResponseBody
    public Map<String, String> saveReview(@PathVariable int reviewKey, @RequestBody Map<String, String> payload) {
        String newComment = payload.get("comment");

        clientReviewService.addNewComment(reviewKey, newComment);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
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
