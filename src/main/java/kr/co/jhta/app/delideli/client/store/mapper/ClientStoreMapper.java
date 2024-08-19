package kr.co.jhta.app.delideli.client.store.mapper;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ClientStoreMapper {
    void insertStore(ClientStoreInfo store);

    ArrayList<ClientStoreInfo> getAllStore(int clientKey);
}
