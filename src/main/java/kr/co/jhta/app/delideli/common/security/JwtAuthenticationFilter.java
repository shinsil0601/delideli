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
        log.info("토큰: {}", token);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            log.info("토큰 유효");
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);
            log.info("유저명: {}", username);
            log.info("역할: {}", role);

            UserDetails userDetails = combinedUserDetailsService.loadUserByUsernameAndType(username, role);

            if (userDetails != null) {
                log.info("유저 상세정보: {}", userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                log.info("사용자 정보 기반 인증 토큰: {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("쿠키가 없음");
            return null;
        }
        for (Cookie cookie : cookies) {
            log.info("받은 쿠키: {}", cookie.getName());
            if (cookie.getName().equals("JWT")) {
                log.info("일치하는 토큰 있음: {}", cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }
}
