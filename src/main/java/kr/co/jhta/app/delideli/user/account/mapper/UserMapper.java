package kr.co.jhta.app.delideli.user.account.mapper;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface UserMapper {
    // 회원가입 시 사용자 정보를 데이터베이스에 삽입
    void insertUser(UserAccount user);

    // 아이디로 사용자 정보를 조회
    Optional<UserAccount> selectUserById(String userId);

    // 이메일로 사용자 정보를 조회
    Optional<UserAccount> selectUserByEmail(String userEmail);

    // 이름과 이메일로 사용자 정보를 조회
    Optional<UserAccount> selectUserByEmailAndName(String userName, String userEmail);

    // 아이디와 이메일로 사용자 정보를 조회
    Optional<UserAccount> selectUserByIdAndEmail(String userId, String email);

    // 비밀번호를 업데이트
    void updatePwUser(UserAccount user);

    // 사용자 주소를 데이터베이스에 삽입
    void insertUserAddress(UserAddress address);

    // 사용자 정보를 수정
    void modifyUser(UserAccount user);

    // 사용자 키로 주소 목록을 조회
    ArrayList<UserAddress> selectUserAddressList(int userKey);

    // 사용자 주소를 수정
    void updateUserAddress(UserAddress address);

    // 기본 주소를 초기화
    void resetDefaultAddress(int userKey);

    // 기본 주소로 설정
    void setDefaultAddress(int addressKey);

    // 사용자 주소를 삭제
    void deleteUserAddress(int addressKey);

    //사용자 포인트충전
    void chargeUserPoint(Map<String, Integer> map);
  
}
