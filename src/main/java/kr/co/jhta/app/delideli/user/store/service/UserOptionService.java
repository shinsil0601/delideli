package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.Option;

import java.util.ArrayList;

public interface UserOptionService {
    Option getOptionById(int aLong);

    ArrayList<Integer> getOptionKeysByMenuKeyAndOptionNames(int menuKey, String optionName);
}
