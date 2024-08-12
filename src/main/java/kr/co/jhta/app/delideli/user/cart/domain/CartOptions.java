package kr.co.jhta.app.delideli.user.cart.domain;

import kr.co.jhta.app.delideli.user.store.domain.Option;
import lombok.Data;

@Data
public class CartOptions {
    private int cartOptionKey;
    private int cartKey;
    private int optionKey;
    private int optionPrice;
    private String optionName;
    private Option option;
    private boolean selected;
}
