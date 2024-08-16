package kr.co.jhta.app.delideli.client.account.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.dto.ClientDTO;

import java.util.Optional;

public interface ClientService {
    //회원가입
    void registerClient(ClientDTO clientDTO);

    // 해당 아이디 정보 받아오기
    ClientAccount findClientById(String userId);

    // 아이디 중복 확인
    boolean checkClientIdExists(String userId);

    // 이메일 중복 확인
    boolean checkClientEmailExists(String email);

    // 이메일로 아이디 전송
    boolean findIdAndSendEmail(String clientEID, String clientName);

    // 사업자번호, 아이디, 사장명이 일치하는 계정의 정보 가져오기
    Optional<ClientAccount> validateClient(String clientId, String clientEID, String clientName);

    // 비밀번호 변경
    void updatePassword(String clientId, String newPassword);

    // 계정 승인여부 확인
    boolean checkAccessAccount(String clientId, String password);

    //내정보 수정
    void modifyClient(ClientDTO clientDTO);

    //비밀번호 변경(로그인)
    void changePwLogin(String clientId, String newPw1);
}
