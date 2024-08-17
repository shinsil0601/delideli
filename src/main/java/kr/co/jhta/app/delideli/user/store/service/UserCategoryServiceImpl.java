package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.Category;
import kr.co.jhta.app.delideli.user.store.mapper.UserCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserCategoryServiceImpl implements UserCategoryService {

    private final UserCategoryMapper userCategoryMapper;

    public UserCategoryServiceImpl(UserCategoryMapper userCategoryMapper) {
        this.userCategoryMapper = userCategoryMapper;
    }

    @Override
    public ArrayList<Category> getAllCategory() {
        return userCategoryMapper.getAllCategory();
    }
}
