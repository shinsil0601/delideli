package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.mapper.ClientStoreMapper;
import kr.co.jhta.app.delideli.client.store.mapper.StoreCategoryMapper;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientStoreServiceImpl implements ClientStoreService {

    @Autowired
    private final ClientStoreMapper  clientStoreMapper;
    @Autowired
    private final StoreCategoryMapper storeCategoryMapper;

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
    public ArrayList<StoreInfo> getAllStore(int clientKey) {
        return clientStoreMapper.getAllStore(clientKey);
    }
}
