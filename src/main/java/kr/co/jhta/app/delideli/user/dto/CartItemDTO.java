package kr.co.jhta.app.delideli.user.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private int cartKey;
    private String menuName;
    private String optionName;
    private int quantity;
}