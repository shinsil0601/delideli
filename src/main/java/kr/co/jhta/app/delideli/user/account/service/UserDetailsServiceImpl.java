package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl - loadUserByUsername 시작 with username: {}", username);

        UserAccount user;
        try {
            user = userMapper.selectUserById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        } catch (Exception e) {
            log.error("UserDetailsServiceImpl - loadUserByUsername 예외 발생: {}", e.getMessage());
            throw e;
        }

        UserDetails userDetails = User.builder()
                .username(user.getUserId())
                .password(user.getUserPw())
                .roles(user.getUserRole())
                .build();

        log.info("UserDetailsServiceImpl - loadUserByUsername 완료 with userDetails: {}", userDetails);
        return userDetails;
    }


}
