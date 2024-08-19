package kr.co.jhta.app.delideli.client.menu.mapper;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOption;
import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ClientOptionMapper {
    ArrayList<ClientOptionGroup> getMenuOptionByMenuKey(int menuKey);

    void addOptionGroup(ClientOptionGroup clientOptionGroup);

    ClientOptionGroup getOptionGroupByKey(int optionGroupKey);

    void updateOptionGroup(ClientOptionGroup clientOptionGroup);

    void deleteOptionGroup(int optionGroupKey);

    void addOption(ClientOption clientOption);

    void updateOptionStatus(int optionKey, String optionStatus);

    void deleteOptionByOptionKey(int optionKey);

    void deleteOptionbyOptionGroupKey(int optionGroupKey);

    ArrayList<ClientOptionGroup> getAllOptionGroupByMenuKey(int menuKey);
}
