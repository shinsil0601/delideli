package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.Option;
import kr.co.jhta.app.delideli.user.store.mapper.UserOptionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserOptionServiceImpl implements UserOptionService {

    private final UserOptionMapper userOptionMapper;

    public UserOptionServiceImpl(UserOptionMapper userOptionMapper) {
        this.userOptionMapper = userOptionMapper;
    }

    @Override
    public Option getOptionById(int optionKey) {
        return userOptionMapper.getOptionById(optionKey);
    }

    @Override
    public ArrayList<Integer> getOptionKeysByMenuKeyAndOptionNames(int menuKey, String optionNames) {
        // 옵션 이름을 콤마로 분리하여 리스트로 변환
        List<String> optionNameList = Arrays.asList(optionNames.split("\\s*,\\s*"));

        // 매퍼를 이용해 optionKey 리스트를 가져옴
        ArrayList<Integer> optionKeys = userOptionMapper.getOptionKeysByMenuKeyAndOptionNames(menuKey, optionNameList);

        return new ArrayList<>(optionKeys);
    }

}
