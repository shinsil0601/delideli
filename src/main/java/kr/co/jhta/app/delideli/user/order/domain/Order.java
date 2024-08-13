package kr.co.jhta.app.delideli.user.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int orderKey;
    private int userKey;
    private int userAddressKey;
    private int storeInfoKey;
    private int couponKey;
    private String orderNo;
    private String orderMethod;
    private int orderPrice;
    private String orderPayment;
    private int orderEstimatedTime;
    private String riderDesc;
    private String shopDesc;
    private LocalDateTime orderRegdate;
    private LocalDateTime orderUpdate;
}
