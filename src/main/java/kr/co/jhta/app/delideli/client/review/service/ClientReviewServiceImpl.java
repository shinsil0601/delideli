package kr.co.jhta.app.delideli.client.review.service;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.order.domain.ClientOrderDetail;
import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import kr.co.jhta.app.delideli.client.review.mapper.ClientReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ClientReviewServiceImpl implements ClientReviewService {
    @Autowired
    private final ClientReviewMapper clientReviewMapper;

    public ClientReviewServiceImpl(ClientReviewMapper clientReviewMapper) {
        this.clientReviewMapper = clientReviewMapper;
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

    @Override
    public void updateComment(int reviewKey, String updatedComment) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewKey", reviewKey);
        params.put("updatedComment", updatedComment);

        clientReviewMapper.updateComment(params);
    }

    @Override
    public void addNewComment(int reviewKey, String newComment) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewKey", reviewKey);
        params.put("newComment", newComment);
        clientReviewMapper.addNewComment(params);
    }

    @Override
    public ArrayList<ClientReview> getAllReviewWithPaging(int clientKey, String storeKey, int page, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("storeKey", storeKey);
        map.put("startNo", (page - 1) * pageSize);
        map.put("pageSize", pageSize);

        return clientReviewMapper.getAllReviewWithPaging(map);
    }

    @Override
    public int getTotalReviewsByStoreKey(int clientKey, String storeKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("storeKey", storeKey);
        return clientReviewMapper.getTotalReviewsByStoreKey(map);
    }

}
