package kr.co.jhta.app.delideli.user.store.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class Menu {
    private int menuKey;
    private int storeInfoKey;
    private int menuGroupKey;
    private String menuName;
    private int menuPrice;
    private String menuImg;
    private String menuStatus;
    private Date menuRegdate;
    private Date menuUpdate;
    private MenuGroup menuGroup;
    private String menuDesc;
}
