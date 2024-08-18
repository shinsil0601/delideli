package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.*;
import kr.co.jhta.app.delideli.user.store.mapper.UserMenuMapper;
import kr.co.jhta.app.delideli.user.review.mapper.UserReviewMapper;
import kr.co.jhta.app.delideli.user.store.mapper.UserStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStoreServiceImpl implements UserStoreService {

    @Autowired
    private UserStoreMapper userStoreMapper;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private UserMenuMapper userMenuMapper;

    @Override
    public ArrayList<StoreInfo> getAllStoresInRegion(String region) {
        return userStoreMapper.getAllStoresInRegion(region);
    }

    @Override
    public ArrayList<StoreInfo> getAllStoresByCategoryAndRegion(int categoryId, String region) {
        return userStoreMapper.getAllStoresByCategoryAndRegion(categoryId, region);
    }

    @Override
    public ArrayList<StoreInfo> searchAllStoresByNameAndRegion(String query, String region) {
        return userStoreMapper.searchAllStoresByNameAndRegion(query, region);
    }

    @Override
    public Double getAverageRatingForStore(int storeInfoKey) {
        return userReviewMapper.getAverageRatingForStore(storeInfoKey);
    }

    @Override
    public Menu getFirstMenuForStore(int storeInfoKey) {
        return userMenuMapper.getFirstMenuForStore(storeInfoKey);
    }

    @Override
    public int getReviewCountForStore(int storeInfoKey) {
        return userReviewMapper.getReviewCountForStore(storeInfoKey);
    }

    @Override
    public StoreInfo getStoreInfoById(int storeInfoKey) {
        return userStoreMapper.getStoreInfoById(storeInfoKey);
    }

    @Override
    public ArrayList<Review> getReviewListByStore(int storeInfoKey) {
        return userReviewMapper.getReviewListByStore(storeInfoKey);
    }

    // 가게 메뉴들을 그룹별로 가져옴
    @Override
    public Map<MenuGroup, ArrayList<Menu>> getMenuGroupedByMenuGroup(int storeInfoKey) {
        ArrayList<Menu> menus = userMenuMapper.getMenuListByStore(storeInfoKey);

        // 메뉴들을 메뉴 그룹으로 묶음
        return menus.stream().collect(Collectors.groupingBy(Menu::getMenuGroup, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public Menu getMenuById(int menuKey) {
        return userMenuMapper.getMenuById(menuKey);
    }

    @Override
    public ArrayList<OptionGroup> getOptionGroupsByMenuId(int menuKey) {
        return userMenuMapper.getOptionGroupsByMenuId(menuKey);
    }

    @Override
    public int getMenuByStoreInfoKeyAndMenuName(int storeInfoKey, String menuName) {
        Integer menuKey = userMenuMapper.getMenuKeyByStoreInfoKeyAndMenuName(storeInfoKey, menuName);
        if (menuKey == null) {
            throw new IllegalArgumentException("해당하는 메뉴를 찾을 수 없습니다: storeInfoKey=" + storeInfoKey + ", menuName=" + menuName);
        }
        return menuKey;
    }

    @Override
    public ArrayList<StoreInfo> getStoresByCategory(int firstCategoryId, int i) {
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", firstCategoryId);
        params.put("limit", i);
        return userStoreMapper.getStoresByCategory(params);
    }

}
