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

        int updatedRows = clientReviewMapper.updateReportReview(reviewKey);
        if (updatedRows > 0) {
            return true; // 신고 성공
        } else {
            ClientReview review = clientReviewMapper.getReviewByKey(reviewKey);
            if (review != null && review.isReportReview()) {
                throw new IllegalStateException("이미 신고된 리뷰입니다.");
            }
            return false; // 신고 실패
        }
    }



}
