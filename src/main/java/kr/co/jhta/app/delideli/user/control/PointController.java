package kr.co.jhta.app.delideli.user.control;

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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class PointController {

    @Autowired
    private final UserService userService;

    //포인트충전 창이동
    @GetMapping("/charge")
    public String charge(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "/user/mypage/mypoint";
    }

    //포인트 충전
    @PostMapping("/charge")
    @ResponseBody
    public Map<String, Object> chargePoint(@AuthenticationPrincipal User user, @RequestParam("amount") int amount, @RequestParam("userKey") String userKey) {
        log.info("Received userKey: {}", userKey); // 여기서 userKey를 출력합니다.

        Map<String, Object> response = new HashMap<>();
        try {
            // 포인트 충전 로직 추가
            userService.chargePoint(userKey, amount);
            log.info("Successfully charged userKey: {}", userKey);
            log.info("Successfully charged amount: {}", amount);
            // 충전 후 사용자 정보를 다시 가져와서 응답에 추가
            UserAccount userAccount = userService.findUserById(user.getUsername());
            response.put("success", true);
            response.put("userPoint", userAccount.getUserPoint());

        } catch (Exception e) {
            response.put("success", false);
        }
        return response;
    }
}
