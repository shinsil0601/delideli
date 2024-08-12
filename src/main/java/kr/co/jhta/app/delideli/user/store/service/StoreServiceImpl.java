package kr.co.jhta.app.delideli.user.store.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.*;
import kr.co.jhta.app.delideli.user.store.mapper.MenuMapper;
import kr.co.jhta.app.delideli.user.review.mapper.ReviewMapper;
import kr.co.jhta.app.delideli.user.store.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MenuMapper menuMapper;

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

    @Override
    public Double getAverageRatingForStore(int storeInfoKey) {
        return reviewMapper.getAverageRatingForStore(storeInfoKey);
    }

    @Override
    public Menu getFirstMenuForStore(int storeInfoKey) {
        return menuMapper.getFirstMenuForStore(storeInfoKey);
    }

    @Override
    public int getReviewCountForStore(int storeInfoKey) {
        return reviewMapper.getReviewCountForStore(storeInfoKey);
    }

    @Override
    public StoreInfo getStoreInfoById(int storeInfoKey) {
        return storeMapper.getStoreInfoById(storeInfoKey);
    }

    @Override
    public ArrayList<Review> getReviewListByStore(int storeInfoKey) {
        return reviewMapper.getReviewListByStore(storeInfoKey);
    }

    // 가게 메뉴들을 그룹별로 가져옴
    @Override
    public Map<MenuGroup, ArrayList<Menu>> getMenuGroupedByMenuGroup(int storeInfoKey) {
        ArrayList<Menu> menus = menuMapper.getMenuListByStore(storeInfoKey);

        // 메뉴들을 메뉴 그룹으로 묶음
        return menus.stream().collect(Collectors.groupingBy(Menu::getMenuGroup, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public Menu getMenuById(int menuKey) {
        return menuMapper.getMenuById(menuKey);
    }

    @Override
    public ArrayList<OptionGroup> getOptionGroupsByMenuId(int menuKey) {
        return menuMapper.getOptionGroupsByMenuId(menuKey);
    }

}
