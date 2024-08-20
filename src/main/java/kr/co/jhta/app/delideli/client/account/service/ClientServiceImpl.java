package kr.co.jhta.app.delideli.client.account.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.dto.ClientDTO;
import kr.co.jhta.app.delideli.client.account.mapper.ClientMapper;
import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
    @Autowired
    private final ClientMapper clientMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    //회원가입
    @Override
    public void registerClient(ClientDTO clientDTO) {
        ClientAccount client = new ClientAccount();
        client.setClientEID(clientDTO.getClientEID());
        client.setClientName(clientDTO.getClientName());
        client.setClientPhone(clientDTO.getClientPhone());
        client.setClientId(clientDTO.getClientId());
        client.setClientPw(passwordEncoder.encode(clientDTO.getClientPw()));
        client.setClientEmail(clientDTO.getClientEmail());
        client.setBankName(clientDTO.getBankName());
        client.setBankAccount(clientDTO.getBankAccount());

        clientMapper.insertClient(client);
    }

    // 비밀번호 변경
    @Override
    public void updatePassword(String clientId, String newPassword) {
        ClientAccount clientAccount = findClientById(clientId);
        clientAccount.setClientPw(passwordEncoder.encode(newPassword));
        clientMapper.updatePwClient(clientAccount);
    }

    // 계정 승인여부 확인
    @Override
    public boolean checkAccessAccount(String clientId, String password) {
        ClientAccount clientAccount = findClientById(clientId);
        return clientMapper.checkAccessAccount(clientAccount);
    }

    // 계정 탈퇴여부 확인
    @Override
    public boolean checkQuitAccount(String clientId, String password) {
        ClientAccount clientAccount = findClientById(clientId);
        return clientMapper.checkQuitAccount(clientAccount);
    }

    //사장님 내정보 수정
    @Override
    public void modifyClient(ClientDTO clientDTO) {
        clientMapper.modifyClient(clientDTO);
    }

    // 아이디 중복 확인
    @Override
    public boolean checkClientIdExists(String clientId) {
        return clientMapper.selectClientById(clientId).isPresent();
    }

    // 이메일 중복 확인
    @Override
    public boolean checkClientEmailExists(String email) {
        return clientMapper.selectClientByEmail(email).isPresent();
    }

    // 해당 아이디 정보 받아오기
    @Override
    public ClientAccount findClientById(String clientId) {
        return clientMapper.selectClientById(clientId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with clientname: " + clientId));
    }

    // 이메일로 아이디 전송
    @Override
    public boolean findIdAndSendEmail(String clientEID, String clientName) {
        Optional<ClientAccount> clientOpt = clientMapper.selectClientByEIDAndName(clientEID, clientName);
        if (clientOpt.isPresent()) {
            ClientAccount client = clientOpt.get();
            emailService.sendEmail(client.getClientEmail(), "delideli 사장 아이디", clientName + "님의 아이디는 " + client.getClientId() + " 입니다.");
            return true;
        }
        return false;
    }

    // 사업자번호, 아이디, 사장명이 일치하는 계정의 정보 가져오기
    @Override
    public Optional<ClientAccount> validateClient(String clientId, String clientEID, String clientName) {
        return clientMapper.selectClientByIdAndEIDAndName(clientId, clientEID, clientName);
    }

    //비밀번호변경(로그인)
    @Override
    public void changePwLogin(String clientId, String newPw1) {
        //log.info("clientId: {}, newPw1!!!!!!!!!!!!!! {}", clientId, newPw1);
        ClientAccount clientAccount = findClientById(clientId);
        clientAccount.setClientPw(passwordEncoder.encode(newPw1));
        clientMapper.changePwLogin(clientAccount);
    }

    @Override
    public void quitClientAccount(String clientId) {
        clientMapper.quitClientAccount(clientId);
    }

}
