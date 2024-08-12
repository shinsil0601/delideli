package kr.co.jhta.app.delideli.user.category.service;

import kr.co.jhta.app.delideli.user.category.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.category.dto.CategoryStoreInfoDTO;

import java.util.List;

public interface UserCategoryService {

    List<CategoryDTO> getCategoryAllList();

    List<CategoryStoreInfoDTO> getCategoryStoreInfo(int num);

    List<CategoryStoreInfoDTO> getAllStores();
}
