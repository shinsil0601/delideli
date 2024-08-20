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
        //log.info("review>>>>>>>>>>>!!!!: {}", reviewList.toString());

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeList);
        model.addAttribute("reviewList", reviewList);

        return "client/review/review.view";
    }

    // 리뷰 신고 처리 (비동기 요청)
    @PostMapping("/reviewReport/{reviewKey}")
    @ResponseBody
    public String reportReview(@PathVariable("reviewKey") int reviewKey) {
        try {
            log.info("reviewKey:!11>>>>>>>>>> {}", reviewKey);
            boolean success = clientReviewService.reportReview(reviewKey); // 신고 처리 서비스 호출
            if (success) {
                return "{\"status\": \"success\"}"; // JSON 형식의 성공 응답
            } else {
                return "{\"status\": \"failure\"}"; // JSON 형식의 실패 응답
            }
        } catch (Exception e) {
            log.error("Review report failed>>>>>>>>>>", e);
            return "{\"status\": \"error\"}"; // JSON 형식의 오류 응답
        }
    }



}
