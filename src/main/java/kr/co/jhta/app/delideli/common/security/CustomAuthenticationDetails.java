package kr.co.jhta.app.delideli.common.security;

import lombok.Getter;

@Getter
public class CustomAuthenticationDetails {
    private final String userType;

    public CustomAuthenticationDetails(String userType) {
        this.userType = userType;
    }

}
