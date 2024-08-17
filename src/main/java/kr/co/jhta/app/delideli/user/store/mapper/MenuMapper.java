package kr.co.jhta.app.delideli.user.store.mapper;

import kr.co.jhta.app.delideli.user.store.domain.Menu;
import kr.co.jhta.app.delideli.user.store.domain.MenuGroup;
import kr.co.jhta.app.delideli.user.store.domain.OptionGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface MenuMapper {

    Menu getFirstMenuForStore(int storeInfoKey);

    ArrayList<Menu> getMenuListByStore(int storeInfoKey);

    Menu getMenuById(int menuKey);

    ArrayList<OptionGroup> getOptionGroupsByMenuId(int menuKey);

    Integer getMenuKeyByStoreInfoKeyAndMenuName(int storeInfoKey, String menuName);
}
