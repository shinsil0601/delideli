package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;
import kr.co.jhta.app.delideli.client.menu.mapper.ClientOptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientOptionServiceImpl implements ClientOptionService{

    @Autowired
    private final ClientOptionMapper clientOptionMapper;

    public ClientOptionServiceImpl(ClientOptionMapper clientOptionMapper) {
        this.clientOptionMapper = clientOptionMapper;
    }

    @Override
    public ArrayList<ClientOptionGroup> getMenuOptionByMenuKey(int menuKey) {
        return clientOptionMapper.getMenuOptionByMenuKey(menuKey);
    }
}
