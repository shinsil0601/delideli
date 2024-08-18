package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;

import java.util.ArrayList;

public interface ClientOptionService {
    ArrayList<ClientOptionGroup> getMenuOptionByMenuKey(int menuKey);
}
