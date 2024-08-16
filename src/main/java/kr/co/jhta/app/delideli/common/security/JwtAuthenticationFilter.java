package kr.co.jhta.app.delideli.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.common.service.CombinedUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CombinedUserDetailsService combinedUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CombinedUserDetailsService combinedUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.combinedUserDetailsService = combinedUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                // 유효한 역할인지 확인
                if (!"ROLE_USER".equals(role) && !"ROLE_CLIENT".equals(role) && !"ROLE_ADMIN".equals(role)) {
                    log.error("인증 실패: 유효하지 않은 역할입니다. 사용자 이름: {}, 역할: {}", username, role);
                    throw new UsernameNotFoundException("유효하지 않은 역할입니다. 사용자 이름: " + username + ", 역할: " + role);
                }

                UserDetails userDetails = combinedUserDetailsService.loadUserByUsernameAndType(username, role);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (UsernameNotFoundException e) {
                log.error("인증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                // 인증 실패시 메인 화면 대신 로그인 화면으로 리디렉션
                response.sendRedirect("/user/login?error=" + e.getMessage());
                return; // 필터 체인을 중단하고 리디렉션
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
