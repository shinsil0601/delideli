package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;

import java.util.ArrayList;

public interface ClientStoreService {
    int insertStore(ClientStoreInfo store);

    void insertStoreCategory(int storeInfoKey, int i);

    ArrayList<ClientStoreInfo> getAllStore(int clientKey);
}
