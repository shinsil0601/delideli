package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;

import java.util.ArrayList;

public interface ClientMenuService {

    ArrayList<ClientMenu> getAllMenu(int storeKey);
}
