package kr.co.jhta.app.delideli.user.cart.domain;

import kr.co.jhta.app.delideli.user.store.domain.Menu;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class Cart {
    private int cartKey;
    private int userKey;
    private int menuKey;
    private int quantity;
    private LocalDateTime cartRegdate;
    private Menu menu;
    private StoreInfo storeInfo;
    private ArrayList<CartOptions> cartOptions;
    private int totalPrice;
}
