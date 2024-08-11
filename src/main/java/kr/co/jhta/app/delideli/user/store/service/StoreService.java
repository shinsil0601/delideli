package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;

import java.util.ArrayList;

public interface StoreService {

    // 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> getAllStoresInRegion(String region);

    // 카테고리와 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> getAllStoresByCategoryAndRegion(int categoryId, String region);

    // 검색어와 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> searchAllStoresByNameAndRegion(String query, String region);

}
