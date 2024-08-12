package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.Option;
import kr.co.jhta.app.delideli.user.store.mapper.OptionMapper;
import org.springframework.stereotype.Service;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionMapper optionMapper;

    public OptionServiceImpl(OptionMapper optionMapper) {
        this.optionMapper = optionMapper;
    }

    @Override
    public Option getOptionById(int optionKey) {
        return optionMapper.getOptionById(optionKey);
    }
}
