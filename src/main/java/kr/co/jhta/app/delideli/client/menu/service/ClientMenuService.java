package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;

import java.util.ArrayList;

public interface ClientMenuService {

    ArrayList<ClientMenu> getAllMenu(int storeKey);

    ArrayList<ClientMenu> searchMenu(int storeKey, String filter, String keyword);

    void updateMenuStatus(int menuKey, String status);

    void deleteMenu(int menuKey);

    void deleteMenuGroup(int menuGroupKey);

    ClientMenu getMenuById(int menuKey);
}
