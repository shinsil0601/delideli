package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import kr.co.jhta.app.delideli.client.review.service.ClientReviewService;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
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

    //사장님 가게목록
    @GetMapping("/review")
    public String clientReview(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientStoreInfo> storeList = clientStoreService.getAllStore(clientAccount.getClientKey());
        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeList);

        return "client/review/review.list";
    }

    //리뷰 상세보기
    @GetMapping("/review/{storeKey}")
    public String clientReview(@AuthenticationPrincipal User user,
                               @PathVariable("storeKey") String storeKey, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientStoreInfo> storeList = clientStoreService.getAllStore(clientAccount.getClientKey());
        ArrayList<ClientReview> reviewList = clientReviewService.getAllReview(clientAccount.getClientKey(), storeKey);

        // 로그로 reportReview 값 확인
        reviewList.forEach(review -> log.info("Review Key!!!!!!!1: {}, ReportReview!!!!!!!111: {}", review.getReviewKey(), review.isReportReview()));

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeList);
        model.addAttribute("reviewList", reviewList);

        return "client/review/review.view";
    }

    // 리뷰 신고 처리 (AJAX 요청)
    @PostMapping("/reviewReport/{reviewKey}")
    @ResponseBody
    public Map<String, Object> reportReview(@PathVariable("reviewKey") int reviewKey) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = clientReviewService.reportReview(reviewKey);
            //og.info("success>>>>>>>>>!!!!! : {}", success);
            if (success) {
                response.put("status", "success");
            } else {
                response.put("status", "failure");
            }
        } catch (IllegalStateException e) {
            response.put("status", "already_reported");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            //log.error("Review report failed!!!!!!!>>  ", e);
            response.put("status", "error");
        }
        return response;

    }



}
