package kr.co.jhta.app.delideli.user.account.domain;

import lombok.Data;

@Data
public class UserAccount {
    private int userKey;
    private String userId;
    private String userPw;
    private String userName;
    private String userNickname;
    private String userBirth;
    private String userPhone;
    private String userEmail;
    private String userProfile;
    private int userPoint;
    private String userRank;
    private String userStatus;
    private String userRole;
    private String userRegdate;
    private String userUpdate;
}
