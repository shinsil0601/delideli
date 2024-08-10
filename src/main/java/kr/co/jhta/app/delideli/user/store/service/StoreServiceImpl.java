package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public ArrayList<StoreInfo> getAllStoresInRegion(String region) {
        return storeMapper.getAllStoresInRegion(region);
    }

    @Override
    public ArrayList<StoreInfo> getAllStoresByCategoryAndRegion(int categoryId, String region) {
        return storeMapper.getAllStoresByCategoryAndRegion(categoryId, region);
    }

    @Override
    public ArrayList<StoreInfo> searchAllStoresByNameAndRegion(String query, String region) {
        return storeMapper.searchAllStoresByNameAndRegion(query, region);
    }

}
