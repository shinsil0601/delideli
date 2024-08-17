package kr.co.jhta.app.delideli.client.menu.mapper;

import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ClientMenuMapper {
    ArrayList<ClientMenu> getAllMenu(int storeKey);

    ArrayList<ClientMenu> searchMenu(int storeKey, String filter, String keyword);

    void updateMenuStatus(int menuKey, String status);

    void deleteMenu(int menuKey);

    void deleteMenuGroup(int menuGroupKey);

    void deleteMenusByGroupKey(int menuGroupKey);

    ClientMenu getMenuById(int menuKey);
}
