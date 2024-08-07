package kr.co.jhta.app.delideli.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private String clientEID;
    private String clientName;
    private String clientPhone;
    private String clientId;
    private String clientPw;
    private String clientEmail;
    private String bankName;
    private String bankAccount;
}
