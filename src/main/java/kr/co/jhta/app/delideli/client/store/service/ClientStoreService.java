package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;

public interface ClientStoreService {
    int insertStore(ClientStoreInfo store);

    void insertStoreCategory(int storeInfoKey, int i);
}
