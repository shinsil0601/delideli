package kr.co.jhta.app.delideli.user.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderRequestDTO {
    private int storeInfoKey;
    private String orderNo;
    private String address;
    private String orderMethod;
    private int totalPayment;
    private String paymentMethod;
    private String riderDesc;
    private String shopDesc;
    private Integer couponKey;
    private ArrayList<CartItemDTO> cartItems;
}