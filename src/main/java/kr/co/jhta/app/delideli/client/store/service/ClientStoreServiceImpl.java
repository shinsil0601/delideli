package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.mapper.ClientStoreMapper;
import kr.co.jhta.app.delideli.client.store.mapper.StoreCategoryMapper;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientStoreServiceImpl implements ClientStoreService {

    @Autowired
    private final ClientStoreMapper  clientStoreMapper;
    @Autowired
    private final StoreCategoryMapper storeCategoryMapper;

    public ClientStoreServiceImpl(ClientStoreMapper clientStoreMapper, StoreCategoryMapper storeCategoryMapper) {
        this.clientStoreMapper = clientStoreMapper;
        this.storeCategoryMapper = storeCategoryMapper;
    }

    @Override
    public int insertStore(ClientStoreInfo store) {
        clientStoreMapper.insertStore(store);
        return store.getStoreInfoKey();
    }

    @Override
    public void insertStoreCategory(int storeInfoKey, int categoryKey) {
        storeCategoryMapper.insertStoreCategory(storeInfoKey, categoryKey);
    }

    @Override
    public ArrayList<StoreInfo> getAllStore(int clientKey) {
        return clientStoreMapper.getAllStore(clientKey);
    }

    //사장님 가게 목록 리스트
    @Override
    public List<ClientStoreInfo> storeList(String clientId) {
        List<ClientStoreInfo> list = clientStoreMapper.getStoreList(clientId);
        return list;
    }

    //사장님 가게 영업일시정지 업데이트
    @Override
    public void updateStorePause(int storeInfoKey, boolean pause) {
        Map<String, Object> params = new HashMap<>();
        params.put("storeInfoKey", storeInfoKey);
        params.put("pause", pause);
        clientStoreMapper.updateStorePause(params);
    }

    //사장님 가게 일시정지 상태 조회
    @Override
    public boolean getStorePauseState(int storeInfoKey) {
        return clientStoreMapper.getStorePauseState(storeInfoKey);
    }

    // 가게 목록 페이징 처리
    @Override
    public List<ClientStoreInfo> getPagedStoreList(Long clientKey, int startNo, int endNo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        return clientStoreMapper.getPagedStoreList(map);
    }

    // 검색어에 따른 가게 목록 페이징 처리
    @Override
    public List<ClientStoreInfo> getPagedKeywordStoreList(Long clientKey, int startNo, int endNo, String keyword) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        map.put("keyword", keyword);
        return clientStoreMapper.getPagedKeywordStoreList(map);
    }

    //가게이름 검색어 총게시물수
    @Override
    public int getTotalKeywordStore(Long clientKey, String keyword) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("keyword", keyword);
        return clientStoreMapper.getTotalKeywordStore(map);
    }

    // 승인 여부에 따른 목록 페이징 처리
    @Override
    public List<ClientStoreInfo> getPagedByAccessStatus(Long clientKey, int startNo, int countPerPage, boolean accessStatus) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("startNo", startNo);
        map.put("countPerPage", countPerPage);
        map.put("accessStatus", accessStatus);
        return clientStoreMapper.getPagedByAccessStatus(map);
    }

    // 검색어 없을 때 총 게시물 수
    @Override
    public int getTotalStore(Long clientKey) {
        return clientStoreMapper.getTotalStore(clientKey);
    }

    // 승인 여부에 따른 총 게시물 수
    @Override
    public int getTotalByAccessStatus(Long clientKey, boolean accessStatus) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("accessStatus", accessStatus);
        return clientStoreMapper.getTotalByAccessStatus(map);
    }

    // 전체 가게 목록 가져오기(영업상태 컨트롤러에 값 조회)
    @Override
    public List<ClientStoreInfo> getAllStores(Long clientKey) {
        return clientStoreMapper.getAllStores(clientKey);
    }
}
