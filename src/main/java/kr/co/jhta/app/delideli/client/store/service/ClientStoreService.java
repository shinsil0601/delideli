package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;

import java.util.ArrayList;
import java.util.List;

public interface ClientStoreService {
    int insertStore(ClientStoreInfo store);

    void insertStoreCategory(int storeInfoKey, int i);

    ArrayList<StoreInfo> getAllStore(int clientKey);

    //사장님 가게 목록 리스트
    List<ClientStoreInfo> storeList(String clientId);

    //사장님 가게 영업일시정지 업데이트
    void updateStorePause(int storeInfoKey, boolean pause);

    // 현재 일시정지 상태를 가져오는 메서드
    boolean getStorePauseState(int storeInfoKey);
    //검색어 없을떄 총게시물수
    int getTotalStore(Long clientKey);

    // 가게 목록 페이징 처리
    List<ClientStoreInfo> getPagedStoreList(Long clientKey, int startNo, int endNo);

    // 검색어에 따른 가게 목록 페이징 처리
    List<ClientStoreInfo> getPagedKeywordStoreList(Long clientKey, int startNo, int endNo, String keyword);

    //가게이름 검색어 총게시물수
    int getTotalKeywordStore(Long clientKey, String keyword);

    // 승인 여부에 따른 총 게시물 수
    int getTotalByAccessStatus(Long clientKey, boolean accessStatus);

    // 승인 여부에 따른 목록 페이징 처리
    List<ClientStoreInfo> getPagedByAccessStatus(Long clientKey, int startNo, int countPerPage, boolean accessStatus);

    // 전체 가게 목록 가져오기(영업상태 컨트롤러에 값 조회)
    List<ClientStoreInfo> getAllStores(Long clientKey);

}
