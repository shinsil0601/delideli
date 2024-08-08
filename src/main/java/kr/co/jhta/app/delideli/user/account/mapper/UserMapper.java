package kr.co.jhta.app.delideli.user.account.mapper;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
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
    void modifyUser(UserAccount user);
<<<<<<< HEAD
}
=======
    ArrayList<UserAddress> selectUserAddressList(Long userKey);

    void updateUserAddress(UserAddress address);

    void resetDefaultAddress(Long userKey);

    void setDefaultAddress(Long addressKey);

    void deleteUserAddress(Long addressKey);
}
>>>>>>> 7add44eef5015ac798dd83196046a5ae72bfbae4
