package kr.co.jhta.app.delideli.user.account.mapper;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void insertUser(UserAccount user);
    Optional<UserAccount> selectUserById(String userId);
    Optional<UserAccount> selectUserByEmail(String userEmail);
    Optional<UserAccount> selectUserByEmailAndName(String userName, String userEmail);
    Optional<UserAccount> selectUserByIdAndEmail(String userId, String email);
    void updatePwUser(UserAccount user);
    void insertUserAddress(UserAddress address);
<<<<<<< HEAD
=======
    void modifyUser(UserAccount user);
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
}
