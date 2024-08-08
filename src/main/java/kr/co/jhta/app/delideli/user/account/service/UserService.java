package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.dto.UserDTO;

import java.util.ArrayList;

public interface UserService {
    // 회원가입
    void registerUser(UserDTO userDTO);

    // 아이디 찾기
    UserAccount findUserById(String username);

    // 아이디 중복 확인
    boolean checkUserIdExists(String userId);

    // 이메일 중복 확인
    boolean checkUserEmailExists(String email);

    // 이메일로 아이디 전송
    boolean findIdAndSendEmail(String userName, String userEmail);

    // 아이디, 이메일 일치 확인
    boolean validateUser(String userId, String userEmail);

    // 비밀번호 수정
    void updatePassword(String userId, String newPassword);

    // 비밀번호 확인
    boolean checkPw(String userId, String userPw);

    // 계정정보 수정
    void modifyUser(UserDTO userDTO);

    // 주소 목록
    ArrayList<UserAddress> userAddressList(Long userKey);

    // 주소 추가
    void addAddress(Long userKey, String newAddress, String newAddrDetail, String newZipcode);

    // 주소 수정
    void modifyAddress(Long addressKey, String newAddress, String newAddrDetail, String newZipcode);

    // 대표 주소 변경
    void setDefaultAddress(Long userKey, Long addressKey);

    // 주소 삭제
    void deleteAddress(Long addressKey);
}
