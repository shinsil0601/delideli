package kr.co.jhta.app.delideli.common.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.mapper.ClientMapper;
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
public class CombinedUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final ClientMapper clientMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Use loadUserByUsernameAndType instead");
    }

    public UserDetails loadUserByUsernameAndType(String username, String role) throws UsernameNotFoundException {
        //log.info("CombinedUserDetailsService - 사용자 이름과 역할로 사용자 정보 로드: {} 역할: {}", username, role);

        if ("ROLE_USER".equals(role)) {
            UserAccount userAccount = userMapper.selectUserById(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. 사용자 이름: " + username));
            return User.builder()
                    .username(userAccount.getUserId())
                    .password(userAccount.getUserPw())
                    .roles(userAccount.getUserRole())
                    .build();
        } else if ("ROLE_CLIENT".equals(role)) {
            ClientAccount clientAccount = clientMapper.selectClientById(username).orElseThrow(() -> new UsernameNotFoundException("클라이언트를 찾을 수 없습니다. 클라이언트 이름: " + username));
            return User.builder()
                    .username(clientAccount.getClientId())
                    .password(clientAccount.getClientPw())
                    .roles(clientAccount.getClientRole())
                    .build();
        } else if ("ROLE_ADMIN".equals(role)) {
            // admin 사용자 하드코딩
            if ("admin".equals(username)) {
                return User.builder()
                        .username("admin")
                        .password("{noop}123") // 비밀번호 암호화 없이 사용
                        .roles("ADMIN")
                        .build();
            }
        }

        throw new UsernameNotFoundException("사용자 또는 클라이언트를 찾을 수 없습니다. 사용자 이름: " + username);
    }
}
