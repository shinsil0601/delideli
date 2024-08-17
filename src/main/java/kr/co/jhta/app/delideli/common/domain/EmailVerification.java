package kr.co.jhta.app.delideli.common.domain;

import lombok.Data;

@Data
public class EmailVerification {
    private String email;
    private String verificationCode;
    private Long timestamp;
}
