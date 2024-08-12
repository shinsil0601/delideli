package kr.co.jhta.app.delideli.user.main.service;

import kr.co.jhta.app.delideli.user.main.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.main.dto.CategoryStoreInfoDTO;
import kr.co.jhta.app.delideli.user.main.dto.StoreCategoryDTO;
import kr.co.jhta.app.delideli.user.main.repository.UserCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MainCategoryServiceImpl implements MainCategoryService {

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
