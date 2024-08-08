package kr.co.jhta.app.delideli.client.account.mapper;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ClientMapper {
    //회원가입
    void insertClient(ClientAccount client);
    // 아이디가 일치하는 계정의 정보 가져오기
    Optional<ClientAccount> selectClientById(String clientId);
    // 이메일이 일치하는 계정의 정보 가져오기
    Optional<ClientAccount> selectClientByEmail(String email);
    // 사업자번호, 아이디가 일치하는 계정의 정보 가져오기
    Optional<ClientAccount> selectClientByEIDAndName(String clientEID, String clientName);
    // 사업자번호, 아이디, 사장명이 일치하는 계정의 정보 가져오기
    Optional<ClientAccount> selectClientByIdAndEIDAndName(String clientId, String clientEID, String clientName);
    // 비밀번호 변경
    void updatePwClient(ClientAccount client);
    //아이디 승인 여부 검토
    boolean checkAccessAccount(ClientAccount clientAccount);
}
