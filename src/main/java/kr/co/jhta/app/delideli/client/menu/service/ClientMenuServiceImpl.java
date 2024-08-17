package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import kr.co.jhta.app.delideli.client.menu.mapper.ClientMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientMenuServiceImpl implements ClientMenuService {

    @Autowired
    private final ClientMenuMapper clientMenuMapper;

    public ClientMenuServiceImpl(ClientMenuMapper clientMenuMapper) {
        this.clientMenuMapper = clientMenuMapper;
    }

    @Override
    public ArrayList<ClientMenu> getAllMenu(int storeKey) {
        return clientMenuMapper.getAllMenu(storeKey);
    }
}
