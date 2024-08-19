package kr.co.jhta.app.delideli.client.store.mapper;

import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClientStoreMapper {
    void insertStore(ClientStoreInfo store);

    ArrayList<ClientStoreInfo> getAllStore(int clientKey);

    void updateStorePause(Map<String, Object> params);

    // 현재 일시정지 상태를 가져오는 메서드
    boolean getStorePauseState(int storeInfoKey);

    ClientStoreInfo getStoreDetail(int storeInfoKey);


    List<ClientStoreInfo> filterStores(Map<String, Object> params);

    int getTotalStores(Map<String, Object> params);

    List<ClientStoreInfo> filterStoresWithPaging(Map<String, Object> params);

    boolean getStoreDeleteState(int storeInfoKey);

    void updateStoreDelete(int storeInfoKey, boolean deleteState);

    void updateStoreInfo(ClientStoreInfo storeInfo);
}
