package kr.co.jhta.app.delideli.user.account.service;

import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.dto.UserDTO;
import kr.co.jhta.app.delideli.user.account.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserMapper userMapper;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Override
    public void registerUser(UserDTO userDTO) {
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

        UserAddress address = new UserAddress();
        address.setUserKey(user.getUserKey());
        address.setAddress(userDTO.getUserAddress());
        address.setZipcode(userDTO.getUserZipcode());
        address.setAddrDetail(userDTO.getUserAddrDetail());
        address.setDefaultAddress(true);

        userMapper.insertUserAddress(address);
    }

    // 아이디 찾기
    @Override
    public UserAccount findUserById(String userId) {
        return userMapper.selectUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userId));
    }

    // 이메일로 아이디 전송
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

    // 아이디, 이메일 일치 확인
    @Override
    public boolean validateUser(String userId, String userEmail) {
        return userMapper.selectUserByIdAndEmail(userId, userEmail).isPresent();
    }

    // 비밀번호 수정
    @Override
    public void updatePassword(String userId, String newPassword) {
        UserAccount userAccount = findUserById(userId);
        userAccount.setUserPw(passwordEncoder.encode(newPassword));
        userMapper.updatePwUser(userAccount);
    }

    // 비밀번호 확인
    @Override
    public boolean checkPw(String userId, String userPw) {
        UserAccount userAccount = findUserById(userId);
        return passwordEncoder.matches(userPw, userAccount.getUserPw());
    }

    // 계정정보 수정
    @Override
    public void modifyUser(UserDTO userDTO) {
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

    // 주소 목록
    @Override
    public ArrayList<UserAddress> userAddressList(Long userKey) {
        return userMapper.selectUserAddressList(userKey);
    }

    // 아이디 중복 확인
    @Override
    public boolean checkUserIdExists(String userId) {
        return userMapper.selectUserById(userId).isPresent();
    }

    // 이메일 중복 확인
    @Override
    public boolean checkUserEmailExists(String email) {
        return userMapper.selectUserByEmail(email).isPresent();
    }

    // 주소 추가
    @Override
    public void addAddress(Long userKey, String newAddress, String newAddrDetail, String newZipcode) {
        UserAddress address = new UserAddress();
        address.setUserKey(userKey);
        address.setAddress(newAddress);
        address.setAddrDetail(newAddrDetail);
        address.setZipcode(newZipcode);
        address.setDefaultAddress(false);
        userMapper.insertUserAddress(address);
    }

    // 주소 수정
    @Override
    public void modifyAddress(Long addressKey, String newAddress, String newAddrDetail, String newZipcode) {
        UserAddress address = new UserAddress();
        address.setUserAddressKey(addressKey);
        address.setAddress(newAddress);
        address.setAddrDetail(newAddrDetail);
        address.setZipcode(newZipcode);
        userMapper.updateUserAddress(address);
    }

    // 대표 주소 변경
    @Override
    public void setDefaultAddress(Long userKey, Long addressKey) {
        userMapper.resetDefaultAddress(userKey);
        userMapper.setDefaultAddress(addressKey);
    }

    // 주소 삭제
    @Override
    public void deleteAddress(Long addressKey) {
        userMapper.deleteUserAddress(addressKey);
    }

    //포인트 충전
    @Override
    public void chargePoint(String userKey, int amount) {
        System.out.println("serviceImp userKey: " + userKey+ " amount: " + amount);
        Map<String, Integer> map = new HashMap<>();

        map.put("userKey", Integer.valueOf(userKey));
        System.out.println(Integer.valueOf(userKey));
        map.put("amount", amount);
        System.out.println("map: " + map);
        userMapper.chargeUserPoint(map);
    }

    // 프로필 이미지 저장
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

        return "../user/images/uploads/" + uniqueFilename;
    }
}
