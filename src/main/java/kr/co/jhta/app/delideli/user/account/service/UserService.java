package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.dto.UserDTO;

public interface UserService {
    void registerUser(UserDTO userDTO);

    UserAccount findUserById(String username);

    boolean checkUserIdExists(String userId);

    boolean checkUserEmailExists(String email);

    boolean findIdAndSendEmail(String userName, String userEmail);

    boolean validateUser(String userId, String userEmail);

    void updatePassword(String userId, String newPassword);
<<<<<<< HEAD
=======

    boolean checkPw(String userId, String userPw);

    void modifyUser(UserDTO userDTO);

>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
}
