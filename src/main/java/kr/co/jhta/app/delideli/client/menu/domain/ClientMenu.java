package kr.co.jhta.app.delideli.client.menu.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientMenu {
    private int menuKey;
    private int storeInfoKey;
    private int menuGroupKey;
    private String menuName;
    private int menuPrice;
    private String menuImg;
    private String menuStatus;
    private Date menuRegdate;
    private Date menuUpdate;
    private ClientMenuGroup clientMenuGroup;
}
