package kr.co.jhta.app.delideli.common.service;

import kr.co.jhta.app.delideli.common.domain.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final Map<String, EmailVerification> verificationStorage = new HashMap<>();

    public void saveVerificationCode(String email, String code) {
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setVerificationCode(code);
        verification.setTimestamp(System.currentTimeMillis());

        verificationStorage.put(email, verification);
    }

    public boolean verifyCode(String email, String code) {
        EmailVerification verification = verificationStorage.get(email);

        if (verification != null && verification.getVerificationCode().equals(code)) {
            verificationStorage.remove(email);
            return true;
        }
        return false;
    }
}
