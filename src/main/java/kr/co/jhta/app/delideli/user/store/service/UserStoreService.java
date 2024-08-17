package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.*;

import java.util.ArrayList;
import java.util.Map;

public interface UserStoreService {

    // 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> getAllStoresInRegion(String region);

    // 카테고리와 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> getAllStoresByCategoryAndRegion(int categoryId, String region);

    // 검색어와 지역에 따른 모든 가게 목록을 반환하는 메서드
    ArrayList<StoreInfo> searchAllStoresByNameAndRegion(String query, String region);

    // 특정 가게의 평균 리뷰 점수를 반환하는 메서드
    Double getAverageRatingForStore(int storeInfoKey);

    // 특정 가게의 첫 번째 메뉴 정보를 반환하는 메서드
    Menu getFirstMenuForStore(int storeInfoKey);

    // 리뷰 개수를 받아오는 메서드
    int getReviewCountForStore(int storeInfoKey);

    // 가게 정보를 받아오는 멧거드
    StoreInfo getStoreInfoById(int storeInfoKey);

    // 가게 리뷰를 가져오는 메서드
    ArrayList<Review> getReviewListByStore(int storeInfoKey);

    // 가게 메뉴를 받아오는 메서드
    Map<MenuGroup,ArrayList<Menu>> getMenuGroupedByMenuGroup(int storeInfoKey);
    
    // 메뉴 정보
    Menu getMenuById(int menuKey);

    // 메뉴 옵션 정보
    ArrayList<OptionGroup> getOptionGroupsByMenuId(int menuKey);

    // 가게키와 메뉴명으로 메뉴키 가져오기
    int getMenuByStoreInfoKeyAndMenuName(int storeInfoKey, String menuName);
}
