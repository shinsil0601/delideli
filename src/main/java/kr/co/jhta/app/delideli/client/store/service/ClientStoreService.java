package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface ClientStoreService {
    int insertStore(ClientStoreInfo store);

    void insertStoreCategory(int storeInfoKey, int i);

    ArrayList<ClientStoreInfo> getAllStore(int clientKey);

    //사장님 가게 영업일시정지 업데이트
    void updateStorePause(int storeInfoKey, boolean pause);

    // 현재 일시정지 상태를 가져오는 메서드
    boolean getStorePauseState(int storeInfoKey);

    ClientStoreInfo getStoreDetail(int storeInfoKey);

    int getTotalStores(int clientKey, String storeAccess, String businessStatus, String keyword);

    List<ClientStoreInfo> filterStoresWithPaging(int clientKey, String storeAccess, String businessStatus, String storeName, int page, int pageSize);

    boolean getStoreDeleteState(int storeInfoKey);

    void updateStoreDelete(int storeInfoKey, boolean newDeleteState);

    List<ClientCategory> getStoreCategories(int storeInfoKey);

    void updateStore(int storeInfoKey, String storeName, String[] categories, String storeAddress, String storeZipcode, String storeAddrDetail, String storePhone, int minOrderAmount, int orderAmount1, int deliveryAmount1, Integer orderAmount2, Integer deliveryAmount2, Integer orderAmount3, Integer deliveryAmount3, String openTime, String closeTime, String storeDetailInfo, String storeOriginInfo, String regFilePath, String reportFilePath, String profileImgPath);

}
