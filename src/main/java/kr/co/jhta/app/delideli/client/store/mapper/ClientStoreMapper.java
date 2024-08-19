package kr.co.jhta.app.delideli.client.store.mapper;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClientStoreMapper {
    void insertStore(ClientStoreInfo store);

    ArrayList<ClientStoreInfo> getAllStore(int clientKey);

    //사장님 가게 목록 리스트
    List<ClientStoreInfo> getStoreList(String clientId);

    void updateStorePause(Map<String, Object> params);

    // 현재 일시정지 상태를 가져오는 메서드
    boolean getStorePauseState(int storeInfoKey);

    // 가게 목록 페이징 처리
    List<ClientStoreInfo> getPagedStoreList(Map<String, Object> params);

    // 검색어에 따른 가게 목록 페이징 처리
    List<ClientStoreInfo> getPagedKeywordStoreList(Map<String, Object> params);

    // 전체 가게 수 반환
    int getTotalStore(Long clientKey);

    // 검색어에 따른 가게 수 반환
    int getTotalKeywordStore(Map<String, Object> params);

    // 승인 여부에 따른 목록 페이징 처리
    List<ClientStoreInfo> getPagedByAccessStatus(HashMap<String, Object> map);

    // 승인 여부에 따른 총 게시물 수
    int getTotalByAccessStatus(HashMap<String, Object> map);

    // 전체 가게 목록 가져오기(영업상태 컨트롤러에 값 조회)
    List<ClientStoreInfo> getAllStores(Long clientKey);
}
