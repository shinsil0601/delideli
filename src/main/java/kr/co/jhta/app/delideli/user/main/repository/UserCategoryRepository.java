package kr.co.jhta.app.delideli.user.main.repository;

import kr.co.jhta.app.delideli.user.main.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.main.dto.CategoryStoreInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserCategoryRepository {

    List<CategoryDTO> getCategoryAllList();

    List<CategoryStoreInfoDTO> selectCategoryStoreInfo(int num);

    List<CategoryStoreInfoDTO> findAllStores();
}
