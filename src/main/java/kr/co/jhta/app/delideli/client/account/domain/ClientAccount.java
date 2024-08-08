package kr.co.jhta.app.delideli.client.account.domain;

import lombok.Data;

@Data
public class ClientAccount {
    private Long clientKey;
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
