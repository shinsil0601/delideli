package kr.co.jhta.app.delideli.client.review.service;

import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import kr.co.jhta.app.delideli.client.review.mapper.ClientReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ClientReviewServiceImpl implements ClientReviewService {
    @Autowired
    private final ClientReviewMapper clientReviewMapper;

    public ClientReviewServiceImpl(ClientReviewMapper clientReviewMapper) {
        this.clientReviewMapper = clientReviewMapper;
    }


    //사장님 가게에 따른 리뷰
    @Override
    public ArrayList<ClientReview> getAllReview(int clientKey, String storeKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("storeKey", storeKey);
        //log.info("map>>>>>!!!!!!{}", map.toString());
        return clientReviewMapper.getreviewList(map);
    }

    // 리뷰 신고 처리
    @Override
    public boolean reportReview(int reviewKey) {
        log.debug("Attempting to report review with key: >>>>>>>>>>>>>> {}", reviewKey);
        int updatedRows = clientReviewMapper.updateReportReview(reviewKey); // 리뷰 신고 업데이트
        log.debug("Number of rows updated:>>>>>>>>>>>>>>>>> {}", updatedRows);
        return updatedRows > 0; // 업데이트 성공 여부 반환
    }



}
