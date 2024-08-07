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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("ClientDetailsServiceImpl - loadUserByUsername 시작 with username: {}", username);

        ClientAccount client;
        try {
            client = (ClientAccount) clientMapper.selectClientById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Client not found with username: " + username));
        } catch (Exception e) {
            log.error("ClientDetailsServiceImpl - loadUserByUsername 예외 발생: {}", e.getMessage());
            throw e;
        }

        UserDetails userDetails = User.builder()
                .username(client.getClientId())
                .password(client.getClientPw())
                .roles(client.getClientRole())
                .build();

        log.info("ClientDetailsServiceImpl - loadUserByUsername 완료 with userDetails: {}", userDetails);
        return userDetails;
    }


}
