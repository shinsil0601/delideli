package kr.co.jhta.app.delideli.admin.member.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberAdmin {
    private int clientKey;
    private String clientEID;
    private String clientName;
    private String clientPhone;
    private String clientId;
    private String clientPw;
    private String clientEmail;
    private String bankName;
    private String bankAccount;
    private boolean clientAccess;
    private boolean clientDelete;
    private String clientRole;
    private String clientRegdate;
    private String clientUpdate;
}
