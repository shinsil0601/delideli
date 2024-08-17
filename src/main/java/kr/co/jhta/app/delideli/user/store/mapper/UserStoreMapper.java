package kr.co.jhta.app.delideli.user.store.mapper;

import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Mapper
public interface UserStoreMapper {

    ArrayList<StoreInfo> getAllStoresInRegion(String region);

    ArrayList<StoreInfo> getAllStoresByCategoryAndRegion(int categoryId, String region);

    ArrayList<StoreInfo> searchAllStoresByNameAndRegion(String query, String region);

    StoreInfo getStoreInfoById(int storeInfoKey);

    ArrayList<StoreInfo> getLikedStores(int userKey);
}
