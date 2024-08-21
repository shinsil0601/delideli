package kr.co.jhta.app.delideli.client.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.dto.ClientDTO;
import kr.co.jhta.app.delideli.client.account.exception.DuplicateClientIdException;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
import kr.co.jhta.app.delideli.common.security.CustomAuthenticationDetails;
import kr.co.jhta.app.delideli.common.security.JwtTokenProvider;
import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.common.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    @Autowired
    private final ClientService clientService;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final ClientStoreService clientStoreService;
    @Autowired
    private final EmailVerificationService emailVerificationService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 로그인창 이동
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        //log.info("로그인창 넘어옴");
        return "client/account/login";
    }

    // 로그인 확인
    @PostMapping("/loginProc")
    public String loginProc(@RequestParam String clientId, @RequestParam String password, HttpServletResponse response) {
        //log.debug("loginProc 호출됨");
        //log.debug("입력된 사용자 이름: {}", clientId);
        try {
            if (clientService.checkAccessAccount(clientId, password) && !clientService.checkQuitAccount(clientId, password)) {
                //log.debug("승인된 계정");

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(clientId, password);
                authenticationToken.setDetails(new CustomAuthenticationDetails("ROLE_CLIENT"));
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 사용자의 역할 가져오기
                String role = authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .findFirst()
                        .orElse("");

                // 유효한 역할인지 확인
                if (!"ROLE_CLIENT".equals(role)) {
                    SecurityContextHolder.clearContext();
                    return "redirect:/client/login?error=" + urlEncode("아이디 또는 비밀번호가 일치하지 않습니다.");
                }

                String token = jwtTokenProvider.generateToken(authentication);
                //log.info("JWT 토큰 : {}", token);

                // JWT 토큰을 쿠키에 추가
                Cookie cookie = jwtTokenProvider.createCookie(token);
                response.addCookie(cookie);

                return "redirect:/client/storeList";
            } else {
                //log.debug("비승인된 계정");
                return "redirect:/client/login?error=" + urlEncode("승인되지 않은 계정입니다.");
            }
        } catch (AuthenticationException e) {
            //log.error("Authentication failed for client: " + clientId);
            //log.error("Authentication failed", e);
            return "redirect:/client/login?error=" + urlEncode("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // URL 인코딩을 위한 유틸리티 메서드
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 인코딩은 항상 지원되므로 이 예외가 발생할 가능성은 거의 없음
            throw new RuntimeException(e);
        }
    }

    // 회원가입 창 이동
    @GetMapping("/register")
    public String register() {
        return "client/account/register";
    }

    // 회원가입
    @PostMapping("/register")
    public String register(ClientDTO clientDTO, Model model) {
        try {
            clientService.registerClient(clientDTO);
            return "redirect:/client/login";
        } catch (DuplicateClientIdException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "client/account/register";
        }
    }

    // 아이디 찾기 창 이동
    @GetMapping("/findId")
    public String findId() {
        return "client/account/findId";
    }

    // 이메일로 아이디 전송
    @PostMapping("/findId")
    @ResponseBody
    public Map<String, Boolean> findId(@RequestParam String clientEID, @RequestParam String clientName) {
        boolean success = clientService.findIdAndSendEmail(clientEID, clientName);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    // 비밀번호 찾기 창 이동
    @GetMapping("/findPw")
    public String findPw() {
        return "client/account/findPw";
    }

    // 비밀번호 변경 링크 생성 및 이메일 전송
    @PostMapping("/sendResetLink")
    @ResponseBody
    public Map<String, String> sendResetLink(@RequestParam String clientId, @RequestParam String clientEID, @RequestParam String clientName) {
        Map<String, String> response = new HashMap<>();
        Optional<ClientAccount> client = clientService.validateClient(clientId, clientEID, clientName);
        if (client.isPresent()) {
            String token = jwtTokenProvider.generateToken(clientId, "RESET_PASSWORD", 36000000L); // 1시간 유효시간
            String resetLink = "http://localhost:8080/client/clientChangePw?token=" + token;
            emailService.sendPasswordResetLink(client.get().getClientEmail(), resetLink);
            response.put("message", "비밀번호 변경 링크가 이메일로 전송되었습니다.");
        } else {
            response.put("error", "사용자 정보가 일치하지 않습니다.");
        }
        return response;
    }

    // 비밀번호 변경 창 이동
    @GetMapping("/clientChangePw")
    public String clientChangePw(@RequestParam String token, Model model) {
        if (jwtTokenProvider.validateToken(token) && "RESET_PASSWORD".equals(jwtTokenProvider.getRoleFromToken(token))) {
            String clientId = jwtTokenProvider.getUsernameFromToken(token);
            model.addAttribute("clientId", clientId);
            return "client/account/changePw";
        } else {
            model.addAttribute("errorMessage", "유효하지 않은 링크입니다.");
            return "redirect:/client/login";
        }
    }

    // 비밀번호 변경
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String clientId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", clientId);
            return "client/account/changePw";
        }
        clientService.updatePassword(clientId, newPassword);
        return "redirect:/client/login";
    }

    // 인증코드 전송
    @PostMapping("/sendVerificationCode")
    @ResponseBody
    public Map<String, String> sendVerificationCode(@RequestParam String email) {
        String verificationCode = emailService.sendEmail(email);
        emailVerificationService.saveVerificationCode(email, verificationCode);
        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 코드가 전송되었습니다.");
        return response;
    }

    // 인증코드 확인
    @PostMapping("/verifyCode")
    @ResponseBody
    public Map<String, Boolean> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = emailVerificationService.verifyCode(email, code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);
        return response;
    }

    // 아이디 중복 확인
    @PostMapping("/checkClientId")
    @ResponseBody
    public boolean checkClientId(@RequestParam String clientId) {
        return clientService.checkClientIdExists(clientId);
    }

    // 이메일 중복 확인
    @PostMapping("/checkClientEmail")
    @ResponseBody
    public boolean checkUserEmail(@RequestParam String email) {
        return clientService.checkClientEmailExists(email);
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/client/login";
    }

    // 마이페이지(로그인한 사용자 정보 확인)
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("on", "myPage");
        return "client/mypage/myPage";
    }

    //마이페이지(내정보 수정 창이동)
    @GetMapping("/infoUpdate")
    public String clientInfoUpdate(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("on", "myPage");
        model.addAttribute("active", "changeInfo");
        return "client/mypage/infoUpdate";
    }

    //마이페이지(내정보 수정)
    @PostMapping("infoUpdate")
    public String clientInfoUpdate(@AuthenticationPrincipal User user, @ModelAttribute ClientDTO clientDTO, Model model){
        if (user != null){
            ClientAccount clientAccount = clientService.findClientById(user.getUsername());
            model.addAttribute("client", clientAccount);
            clientDTO.setClientId(clientAccount.getClientId());
        }
        clientService.modifyClient(clientDTO);
        model.addAttribute("on", "myPage");
        return "redirect:/client/infoUpdate";
    }

    //마이페이지(비밀번호변경 창이동)
    @GetMapping("changePwLogin")
    public String changePwLogin(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("on", "myPage");
        model.addAttribute("active", "changePw");
        return "client/mypage/changePwLogin";
    }

    // 마이페이지(비밀번호변경)
    @PostMapping("changePwLogin")
    public String changePwLogin(@AuthenticationPrincipal User user, @ModelAttribute ClientDTO clientDTO,
                                @RequestParam String clientId, @RequestParam String clientPw1, @RequestParam String newPw1,
                                Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("on", "myPage");
        // 기존 비밀번호와 입력한 비밀번호가 일치하지 않으면 리턴
        if (!passwordEncoder.matches(clientPw1, clientService.findClientById(clientId).getClientPw())) {
            ClientAccount clientAccount = clientService.findClientById(user.getUsername());
            clientDTO.setClientId(clientAccount.getClientId());
            model.addAttribute("client", clientAccount);
            redirectAttributes.addFlashAttribute("message", "기존 비밀번호가 일치하지 않습니다.");
            return "redirect:/client/changePwLogin";
        }
        clientService.changePwLogin(clientId, newPw1);
        redirectAttributes.addFlashAttribute("message", "비밀번호 변경이 완료되었습니다.");
        return "redirect:/client/infoUpdate";
    }

    @GetMapping("/quit")
    public String quit(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("on", "myPage");
        model.addAttribute("active", "quit");
        return "client/mypage/quit";
    }

    @PostMapping("/quit")
    @ResponseBody
    public String quit(@AuthenticationPrincipal User user,
                       @RequestParam("inputClientPw") String inputClientPw,
                       HttpServletResponse response) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());

        if (!passwordEncoder.matches(inputClientPw, clientAccount.getClientPw())) {
            return "failure";
        }

        clientService.quitClientAccount(clientAccount.getClientId());

        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "success";
    }

}
