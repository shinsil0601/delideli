package kr.co.jhta.app.delideli.client.store.mapper;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientStoreMapper {
    void insertStore(ClientStoreInfo store);
}
