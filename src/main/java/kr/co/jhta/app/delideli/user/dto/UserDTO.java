package kr.co.jhta.app.delideli.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    // 계정
    private String userId;
    private String userPw;
    private String userName;
    private String userNickname;
    private String userBirth;
    private String userPhone;
    private String userEmail;
    private MultipartFile userProfile;

    // 주소
    private String userAddress;
    private String userZipcode;
    private String userAddrDetail;
}
