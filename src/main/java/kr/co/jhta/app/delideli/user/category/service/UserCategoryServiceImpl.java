package kr.co.jhta.app.delideli.user.category.service;

import kr.co.jhta.app.delideli.user.category.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.category.dto.CategoryStoreInfoDTO;
import kr.co.jhta.app.delideli.user.category.repository.UserCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCategoryServiceImpl implements UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;

    @Override
    public List<CategoryDTO> getCategoryAllList() {
        List<CategoryDTO> list = userCategoryRepository.getCategoryAllList();
        return list;
    }

    @Override
    public List<CategoryStoreInfoDTO> getCategoryStoreInfo(int num) {
        List<CategoryStoreInfoDTO> list = userCategoryRepository.selectCategoryStoreInfo(num);
        return list;
    }

    @Override
    public List<CategoryStoreInfoDTO> getAllStores() {
        List<CategoryStoreInfoDTO> list = userCategoryRepository.findAllStores();
        return list;
    }
}
