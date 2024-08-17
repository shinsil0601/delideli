package kr.co.jhta.app.delideli.client.store.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreCategoryMapper {
    void insertStoreCategory(int storeInfoKey, int categoryKey);
}
