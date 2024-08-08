package kr.co.jhta.app.delideli.user.account.domain;

import lombok.Data;

@Data
public class UserAddress {
    private Long userAddressKey;
    private Long userKey;
    private String address;
    private String zipcode;
    private String addrDetail;
    private boolean defaultAddress;
    private String addressRegdt;
    private String addressUpdt;
}
