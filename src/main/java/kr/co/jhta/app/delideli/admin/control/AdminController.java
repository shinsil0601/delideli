package kr.co.jhta.app.delideli.admin.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.jhta.app.delideli.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "123";

    private final JwtTokenProvider jwtTokenProvider;

    // 로그인 페이지
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return "admin/account/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model) {
        if (ADMIN_ID.equals(username) && ADMIN_PW.equals(password)) {
            // 인증 성공 시 JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(username, "ROLE_ADMIN", 604800000L);
            Cookie cookie = jwtTokenProvider.createCookie(token);
            response.addCookie(cookie);

            // 세션에 admin 속성 추가
            session.setAttribute("admin", true);
            return "redirect:/admin/overview";
        } else {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "admin/account/login";
        }
    }

    // 관리자 OVERVIEW로 이동
    @GetMapping("/overview")
    public String overview(Model model) {
        return "admin/overview/overview";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        session.invalidate();
        return "redirect:/admin/login";
    }
}
