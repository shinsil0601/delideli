package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.dto.UserDTO;
import kr.co.jhta.app.delideli.user.account.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
<<<<<<< HEAD
=======
@Slf4j
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserMapper userMapper;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserDTO userDTO) {
        // 계정 정보 저장
        UserAccount user = new UserAccount();
        user.setUserId(userDTO.getUserId());
        user.setUserPw(passwordEncoder.encode(userDTO.getUserPw()));
        user.setUserName(userDTO.getUserName());
        user.setUserNickname(userDTO.getUserNickname());
        user.setUserBirth(userDTO.getUserBirth());
        user.setUserPhone(userDTO.getUserPhone());
        user.setUserEmail(userDTO.getUserEmail());

        MultipartFile profileFile = userDTO.getUserProfile();
        if (!profileFile.isEmpty()) {
            String profilePath = saveProfileImage(profileFile);
            user.setUserProfile(profilePath);
        }
        userMapper.insertUser(user);

        // 주소 정보 저장
        UserAddress address = new UserAddress();
        address.setUserKey(user.getUserKey());
        address.setAddress(userDTO.getUserAddress());
        address.setZipcode(userDTO.getUserZipcode());
        address.setAddrDetail(userDTO.getUserAddrDetail());
        address.setDefaultAddress(true);

        userMapper.insertUserAddress(address);
    }

    @Override
    public UserAccount findUserById(String userId) {
        return userMapper.selectUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userId));
    }

    @Override
    public boolean findIdAndSendEmail(String userName, String userEmail) {
        Optional<UserAccount> userOpt = userMapper.selectUserByEmailAndName(userName, userEmail);
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            emailService.sendEmail(userEmail, "delideli 유저 아이디", userName + "님의 아이디는 " + user.getUserId() + " 입니다.");
            return true;
        }
        return false;
    }

    @Override
    public boolean validateUser(String userId, String userEmail) {
        return userMapper.selectUserByIdAndEmail(userId, userEmail).isPresent();
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        UserAccount userAccount = findUserById(userId);
        userAccount.setUserPw(passwordEncoder.encode(newPassword));
        userMapper.updatePwUser(userAccount);
    }

    @Override
<<<<<<< HEAD
=======
    public boolean checkPw(String userId, String userPw) {
        UserAccount userAccount = findUserById(userId);
        return passwordEncoder.matches(userPw, userAccount.getUserPw());
    }

    @Override
    public void modifyUser(UserDTO userDTO) {
        // 계정 정보 저장
        UserAccount user = new UserAccount();
        user.setUserId(userDTO.getUserId());
        user.setUserName(userDTO.getUserName());
        user.setUserNickname(userDTO.getUserNickname());
        user.setUserBirth(userDTO.getUserBirth());
        user.setUserPhone(userDTO.getUserPhone());
        user.setUserEmail(userDTO.getUserEmail());

        MultipartFile profileFile = userDTO.getUserProfile();
        if (!profileFile.isEmpty()) {
            String profilePath = saveProfileImage(profileFile);
            user.setUserProfile(profilePath);
        } else {
            user.setUserProfile(null); // 프로필 이미지가 비어 있는 경우 null로 설정
        }
        userMapper.modifyUser(user);

    }

    @Override
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
    public boolean checkUserIdExists(String userId) {
        return userMapper.selectUserById(userId).isPresent();
    }

    @Override
    public boolean checkUserEmailExists(String email) {
        return userMapper.selectUserByEmail(email).isPresent();
    }

    private String saveProfileImage(MultipartFile file) {
        String uploadDir = "src/main/resources/static/user/images/uploads/";
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        String filePath = uploadDir + uniqueFilename;

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
        return "/user/images/uploads/" + uniqueFilename;
=======
        return "../user/images/uploads/" + uniqueFilename;
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
    }
}
