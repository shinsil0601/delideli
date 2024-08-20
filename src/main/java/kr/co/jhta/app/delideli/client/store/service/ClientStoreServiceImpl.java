package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.mapper.ClientCategoryMapper;
import kr.co.jhta.app.delideli.client.store.mapper.ClientStoreMapper;
import kr.co.jhta.app.delideli.client.store.mapper.StoreCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientStoreServiceImpl implements ClientStoreService {

    @Autowired
    private final ClientStoreMapper  clientStoreMapper;
    @Autowired
    private final StoreCategoryMapper storeCategoryMapper;
    @Autowired
    private ClientCategoryMapper clientCategoryMapper;

    public ClientStoreServiceImpl(ClientStoreMapper clientStoreMapper, StoreCategoryMapper storeCategoryMapper) {
        this.clientStoreMapper = clientStoreMapper;
        this.storeCategoryMapper = storeCategoryMapper;
    }

    @Override
    public int insertStore(ClientStoreInfo store) {
        clientStoreMapper.insertStore(store);
        return store.getStoreInfoKey();
    }

    @Override
    public void insertStoreCategory(int storeInfoKey, int categoryKey) {
        storeCategoryMapper.insertStoreCategory(storeInfoKey, categoryKey);
    }

    @Override
    public ArrayList<ClientStoreInfo> getAllStore(int clientKey) {
        return clientStoreMapper.getAllStore(clientKey);
    }

    //사장님 가게 영업일시정지 업데이트
    @Override
    public void updateStorePause(int storeInfoKey, boolean pause) {
        Map<String, Object> params = new HashMap<>();
        params.put("storeInfoKey", storeInfoKey);
        params.put("pause", pause);
        clientStoreMapper.updateStorePause(params);
    }

    //사장님 가게 일시정지 상태 조회
    @Override
    public boolean getStorePauseState(int storeInfoKey) {
        return clientStoreMapper.getStorePauseState(storeInfoKey);
    }

    @Override
    public ClientStoreInfo getStoreDetail(int storeInfoKey) {
        return clientStoreMapper.getStoreDetail(storeInfoKey);
    }

    @Override
    public int getTotalStores(int clientKey, String storeAccess, String businessStatus, String storeName) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientKey", clientKey);
        params.put("storeAccess", storeAccess);
        params.put("businessStatus", businessStatus);
        params.put("storeName", storeName);
        return clientStoreMapper.getTotalStores(params);
    }

    @Override
    public List<ClientStoreInfo> filterStoresWithPaging(int clientKey, String storeAccess, String businessStatus, String storeName, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientKey", clientKey);
        params.put("storeAccess", storeAccess);
        params.put("businessStatus", businessStatus);
        params.put("storeName", storeName);
        params.put("startNo", (page - 1) * pageSize);
        params.put("pageSize", pageSize);
        return clientStoreMapper.filterStoresWithPaging(params);
    }

    @Override
    public boolean getStoreDeleteState(int storeInfoKey) {
        return clientStoreMapper.getStoreDeleteState(storeInfoKey);
    }

    @Override
    public void updateStoreDelete(int storeInfoKey, boolean deleteState) {
        clientStoreMapper.updateStoreDelete(storeInfoKey, deleteState);

    }

    @Override
    public List<ClientCategory> getStoreCategories(int storeInfoKey) {
        return clientCategoryMapper.getStoreCategories(storeInfoKey);
    }

    @Override
    public void updateStore(int storeInfoKey, String storeName, String[] categories, String storeAddress, String storeZipcode,
                            String storeAddrDetail, String storePhone, int minOrderAmount, int orderAmount1, int deliveryAmount1,
                            Integer orderAmount2, Integer deliveryAmount2, Integer orderAmount3, Integer deliveryAmount3,
                            String openTime, String closeTime, String storeDetailInfo, String storeOriginInfo,
                            String storeBusinessRegistrationFile, String storeBusinessReportFile, String storeProfileImg) {

        // 1. Store 정보 업데이트
        ClientStoreInfo storeInfo = new ClientStoreInfo();
        storeInfo.setStoreInfoKey(storeInfoKey);
        storeInfo.setStoreName(storeName);
        storeInfo.setStoreAddress(storeAddress);
        storeInfo.setStoreZipcode(storeZipcode);
        storeInfo.setStoreAddrDetail(storeAddrDetail);
        storeInfo.setStorePhone(storePhone);
        storeInfo.setMinOrderAmount(minOrderAmount);
        storeInfo.setOrderAmount1(orderAmount1);
        storeInfo.setDeliveryAmount1(deliveryAmount1);
        storeInfo.setOrderAmount2(orderAmount2);
        storeInfo.setDeliveryAmount2(deliveryAmount2);
        storeInfo.setOrderAmount3(orderAmount3);
        storeInfo.setDeliveryAmount3(deliveryAmount3);
        storeInfo.setOpenTime(openTime);
        storeInfo.setCloseTime(closeTime);
        storeInfo.setStoreDetailInfo(storeDetailInfo);
        storeInfo.setStoreOriginInfo(storeOriginInfo);
        storeInfo.setStoreBusinessRegistrationFile(storeBusinessRegistrationFile);
        storeInfo.setStoreBusinessReportFile(storeBusinessReportFile);
        storeInfo.setStoreProfileImg(storeProfileImg);

        clientStoreMapper.updateStoreInfo(storeInfo);

        // 2. 기존 카테고리 삭제
        storeCategoryMapper.deleteStoreCategories(storeInfoKey);

        // 3. 새로운 카테고리 삽입
        if (categories != null && categories.length > 0) {
            for (String categoryKey : categories) {
                storeCategoryMapper.insertStoreCategory(storeInfoKey, Integer.parseInt(categoryKey));
            }
        }
    }

    public List<ClientStoreInfo> filterStores(int clientKey, String storeAccess, String businessStatus, String storeName) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientKey", clientKey);
        params.put("storeAccess", storeAccess);
        params.put("businessStatus", businessStatus);
        params.put("storeName", storeName);
        return clientStoreMapper.filterStores(params);
    }
}
