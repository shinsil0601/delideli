package kr.co.jhta.app.delideli.client.account.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.mapper.ClientMapper;
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
public class ClientDetailsServiceImpl implements UserDetailsService {

    private final ClientMapper clientMapper;

    // 클라이언트를 username (유저 아이디) 으로 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //log.info("ClientDetailsServiceImpl - loadUserByUsername 시작 with username: {}", username);

        ClientAccount client;
        try {
            // username (유저 아이디) 으로 클라이언트를 조회
            client = clientMapper.selectClientById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Client not found with username: " + username));
        } catch (Exception e) {
            //log.error("ClientDetailsServiceImpl - loadUserByUsername 예외 발생: {}", e.getMessage());
            throw e;
        }

        // UserDetails 객체 생성
        UserDetails userDetails = User.builder()
                .username(client.getClientId())
                .password(client.getClientPw())
                .roles(client.getClientRole())
                .build();

        //log.info("ClientDetailsServiceImpl - loadUserByUsername 완료 with userDetails: {}", userDetails);
        return userDetails;
    }
}
