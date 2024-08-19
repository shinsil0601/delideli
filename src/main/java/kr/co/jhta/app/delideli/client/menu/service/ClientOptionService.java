package kr.co.jhta.app.delideli.client.menu.service;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOption;
import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;

import java.util.ArrayList;

public interface ClientOptionService {
    ArrayList<ClientOptionGroup> getMenuOptionByMenuKey(int menuKey);

    void addOptionGroup(ClientOptionGroup clientOptionGroup);

    ClientOptionGroup getOptionGroupByKey(int optionGroupKey);

    void updateOptionGroup(ClientOptionGroup clientOptionGroup);

    void deleteOptionGroup(int optionGroupKey);

    void addOption(ClientOption clientOption);

    void updateOptionStatus(int optionKey, String optionStatus);

    void deleteOptionByOptionKey(int optionKey);

    void deleteOptionbyOptionGroupKey(int optionGroupKey);
}
