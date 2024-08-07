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
}
