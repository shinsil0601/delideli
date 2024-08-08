package kr.co.jhta.app.delideli.user.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.common.security.CustomAuthenticationDetails;
import kr.co.jhta.app.delideli.common.security.JwtTokenProvider;
import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.common.service.EmailVerificationService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.dto.UserDTO;
import kr.co.jhta.app.delideli.user.account.exception.DuplicateUserIdException;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final EmailVerificationService emailVerificationService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final PasswordEncoder passwordEncoder;


    @GetMapping("home")
    public String home(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            log.info("User is authenticated: {}", user.getUsername());
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        } else {
            log.info("User is not authenticated");
        }
        return "index";
    }

    // 로그인창 이동
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        //log.info("로그인창 넘어옴");
        return "user/account/login";
    }

    // 로그인 (JWT 토큰 생성 및 캐시에 저장)
    @PostMapping("/loginProc")
    public String loginProc(@RequestParam String userId, @RequestParam String password, HttpServletResponse response) {
        log.debug("loginProc 호출됨");
        log.debug("입력된 사용자 이름: {}", userId);
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);
            authenticationToken.setDetails(new CustomAuthenticationDetails("ROLE_USER"));
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);
            log.info("JWT 토큰 : {}", token);

            // JWT 토큰을 쿠키에 추가
            Cookie cookie = jwtTokenProvider.createCookie(token);
            response.addCookie(cookie);

            return "redirect:home";
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: " + userId);
            log.error("Authentication failed", e);
            return "redirect:/user/login?error="  + urlEncode("아이디 또는 비밀번호가 일치하지 않습니다.");
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
        return "user/account/register";
    }

    // 회원가입
    @PostMapping("/register")
    public String registerUser(UserDTO userDTO, Model model) {
        try {
            userService.registerUser(userDTO);
            return "redirect:/user/login";
        } catch (DuplicateUserIdException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/account/register";
        }
    }

    // 아이디 찾기 창 이동
    @GetMapping("/findId")
    public String findId() {
        return "user/account/findId";
    }

    // 이메일로 아이디 전송
    @PostMapping("/findId")
    @ResponseBody
    public Map<String, Boolean> findId(@RequestParam String userName, @RequestParam String userEmail) {
        boolean success = userService.findIdAndSendEmail(userName, userEmail);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    // 비밀번호 찾기 창 이동
    @GetMapping("/findPw")
    public String findPw() {
        return "user/account/findPw";
    }

    // 비밀번호 변경 링크 생성 및 이메일 전송
    @PostMapping("/sendResetLink")
    @ResponseBody
    public Map<String, String> sendResetLink(@RequestParam String userId, @RequestParam String userEmail) {
        Map<String, String> response = new HashMap<>();
        if (userService.validateUser(userId, userEmail)) {
            String token = jwtTokenProvider.generateToken(userId, "RESET_PASSWORD", 3600000L); // 1시간 유효기간
            String resetLink = "http://localhost:8080/user/userChangePw?token=" + token;
            emailService.sendPasswordResetLink(userEmail, resetLink);
            response.put("message", "비밀번호 변경 링크가 이메일로 전송되었습니다.");
        } else {
            response.put("error", "사용자 정보가 일치하지 않습니다.");
        }
        return response;
    }

    // 비밀번호 변경 창 이동 (비로그인시)
    @GetMapping("/userChangePw")
    public String userChangePw(@RequestParam String token, Model model) {
        if (jwtTokenProvider.validateToken(token) && "RESET_PASSWORD".equals(jwtTokenProvider.getRoleFromToken(token))) {
            String userId = jwtTokenProvider.getUsernameFromToken(token);
            model.addAttribute("userId", userId);
            return "user/account/changePw";
        } else {
            model.addAttribute("errorMessage", "유효하지 않은 링크입니다.");
            return "redirect:/user/login";
        }
    }

    // 비밀번호 변경 (비로그인시)
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String userId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            return "user/account/changePw";
        }
        userService.updatePassword(userId, newPassword);
        return "redirect:/user/login";
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
    @PostMapping("/checkUserId")
    @ResponseBody
    public boolean checkUserId(@RequestParam String userId) {
        return userService.checkUserIdExists(userId);
    }

    // 이메일 중복 확인
    @PostMapping("/checkUserEmail")
    @ResponseBody
    public boolean checkUserEmail(@RequestParam String email) {
        return userService.checkUserEmailExists(email);
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

        return "redirect:/user/login";
    }

    // 마이페이지(로그인한 사용자 정보 확인)
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/myPage";
    }

    // 내 정보 확인
    @GetMapping("/checkAccount")
    public String checkAccount(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/checkAccount";
    }

    // 비밀번호 확인
    @PostMapping("/checkPw")
    public String checkPw(@AuthenticationPrincipal User user,@RequestParam String userId, @RequestParam String userPw, Model model) {
        if (userService.checkPw(userId, userPw)) {
            return "redirect:/user/modifyUser";
        } else {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "user/mypage/checkAccount";
        }
    }

    // 내 정보 수정 창 이동
    @GetMapping("/modifyUser")
    public String modifyUser(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/modifyUser";
    }
    
    // 내 정보 수정
    @PostMapping("/modifyUser")
    public String modifyUser(@ModelAttribute UserDTO userDTO, Model model) {
        userService.modifyUser(userDTO);
        return "redirect:/user/myPage";
    }

    // 비밀번호 변경 (로그인시)
    @GetMapping("/updatePw")
    public String updatePw(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/updatePw";
    }

    // 비밀번호 업데이트
    @PostMapping("/updatePassword")
    public String updatePassword(@AuthenticationPrincipal User user, @RequestParam String userId, @RequestParam String userPw, @RequestParam String newPassword, Model model) {
        // 기존 비밀번호와 입력한 비밀번호가 일치하는 지 확인
        if (!passwordEncoder.matches(userPw, userService.findUserById(userId).getUserPw())) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
            return "user/mypage/updatePw";
        }

        userService.updatePassword(userId, newPassword);

        return "redirect:/user/myPage";
    }

}
