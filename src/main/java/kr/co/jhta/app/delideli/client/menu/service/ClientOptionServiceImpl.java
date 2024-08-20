package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOption;
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

    @Override
    public void addOptionGroup(ClientOptionGroup clientOptionGroup) {
        clientOptionMapper.addOptionGroup(clientOptionGroup);
    }

    @Override
    public ClientOptionGroup getOptionGroupByKey(int optionGroupKey) {
        return clientOptionMapper.getOptionGroupByKey(optionGroupKey);
    }

    @Override
    public void updateOptionGroup(ClientOptionGroup clientOptionGroup) {
        clientOptionMapper.updateOptionGroup(clientOptionGroup);
    }

    @Override
    public void deleteOptionGroup(int optionGroupKey) {
        clientOptionMapper.deleteOptionGroup(optionGroupKey);
    }

    @Override
    public void addOption(ClientOption clientOption) {
        clientOptionMapper.addOption(clientOption);
    }

    @Override
    public void updateOptionStatus(int optionKey, String optionStatus) {
        clientOptionMapper.updateOptionStatus(optionKey, optionStatus);
    }

    @Override
    public void deleteOptionByOptionKey(int optionKey) {
        clientOptionMapper.deleteOptionByOptionKey(optionKey);
    }

    @Override
    public void deleteOptionbyOptionGroupKey(int optionGroupKey) {
        clientOptionMapper.deleteOptionbyOptionGroupKey(optionGroupKey);
    }
}
