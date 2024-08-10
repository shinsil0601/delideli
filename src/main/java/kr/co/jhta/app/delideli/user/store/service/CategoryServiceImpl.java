package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.Category;
import kr.co.jhta.app.delideli.user.store.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ArrayList<Category> getAllCategory() {
        return categoryMapper.getAllCategory();
    }
}
