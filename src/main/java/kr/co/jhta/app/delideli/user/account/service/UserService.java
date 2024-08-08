package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.dto.UserDTO;

import java.util.ArrayList;

public interface UserService {
    void registerUser(UserDTO userDTO);

    UserAccount findUserById(String username);

    boolean checkUserIdExists(String userId);

    boolean checkUserEmailExists(String email);

    boolean findIdAndSendEmail(String userName, String userEmail);

    boolean validateUser(String userId, String userEmail);

    void updatePassword(String userId, String newPassword);

    boolean checkPw(String userId, String userPw);

    void modifyUser(UserDTO userDTO);

    ArrayList<UserAddress> userAddressList(Long userKey);

    void addAddress(Long userKey, String newAddress, String newAddrDetail, String newZipcode);

    void modifyAddress(Long addressKey, String newAddress, String newAddrDetail, String newZipcode);

    void setDefaultAddress(Long userKey, Long addressKey);

    void deleteAddress(Long addressKey);
}
