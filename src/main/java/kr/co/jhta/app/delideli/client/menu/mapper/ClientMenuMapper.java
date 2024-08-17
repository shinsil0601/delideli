package kr.co.jhta.app.delideli.client.menu.mapper;

import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ClientMenuMapper {
    ArrayList<ClientMenu> getAllMenu(int storeKey);
}
