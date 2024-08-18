package kr.co.jhta.app.delideli.client.menu.mapper;

import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ClientOptionMapper {
    ArrayList<ClientOptionGroup> getMenuOptionByMenuKey(int menuKey);
}
