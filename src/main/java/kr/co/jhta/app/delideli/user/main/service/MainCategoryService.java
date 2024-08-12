package kr.co.jhta.app.delideli.user.main.service;

import kr.co.jhta.app.delideli.user.main.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.main.dto.CategoryStoreInfoDTO;

import java.util.List;

public interface MainCategoryService {

    List<CategoryDTO> getCategoryAllList();

    List<CategoryStoreInfoDTO> getCategoryStoreInfo(int num);

    List<CategoryStoreInfoDTO> getAllStores();
}
