package kr.co.jhta.app.delideli.user.store.mapper;

import kr.co.jhta.app.delideli.user.store.domain.Option;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OptionMapper {
    Option getOptionById(int optionKey);

    ArrayList<Integer> getOptionKeysByMenuKeyAndOptionNames(int menuKey, List<String> optionNameList);
}
