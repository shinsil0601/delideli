package kr.co.jhta.app.delideli.admin.control;

import kr.co.jhta.app.delideli.admin.member.domain.AdminMemberAdmin;
import kr.co.jhta.app.delideli.admin.member.service.AdminMemberService;
import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminMemberController {
    @Autowired
    private AdminMemberService adminMemberService;

    //사장님 회원 목록 조회
    @GetMapping("/member")
    public String member(Model model) {
        List<AdminMemberAdmin> clientAccount = adminMemberService.getAllclientList();
        model.addAttribute("clientAccount", clientAccount);

        return "admin/member/client.list";
    }

    // 승인 상태를 토글하는 요청 처리
    @PostMapping("/member/clientAccess")
    @ResponseBody
    public Map<String, Object> toggleAccess(@RequestParam int clientKey) {
        log.info("clientKey>>>>>>>!!!!!: {}", clientKey);
        int newAccessStatus = adminMemberService.toggleClientAccess(clientKey);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("clientAccess", newAccessStatus);
        return response;
    }

}
