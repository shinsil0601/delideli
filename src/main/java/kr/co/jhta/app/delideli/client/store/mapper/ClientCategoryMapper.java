package kr.co.jhta.app.delideli.client.store.mapper;

import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ClientCategoryMapper {
    ArrayList<ClientCategory> getAllCategory();

    List<ClientCategory> getStoreCategories(int storeInfoKey);

}
