package kr.co.jhta.app.delideli.user.store.mapper;

import kr.co.jhta.app.delideli.user.store.domain.Option;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OptionMapper {
    Option getOptionById(int optionKey);
}
