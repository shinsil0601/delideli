package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;

import java.util.ArrayList;

public interface ClientStoreService {
    int insertStore(ClientStoreInfo store);

    void insertStoreCategory(int storeInfoKey, int i);

    ArrayList<StoreInfo> getAllStore(int clientKey);
}
