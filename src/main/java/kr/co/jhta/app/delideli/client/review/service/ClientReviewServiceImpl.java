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


    // 사장님 가게에 따른 리뷰와 관련 주문, 주문 상세 정보를 포함한 리스트를 가져옴
    @Override
    public ArrayList<ClientReview> getAllReview(int clientKey, String storeKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("storeKey", storeKey);

        // 리뷰 리스트 가져오기
        ArrayList<ClientReview> reviewList = clientReviewMapper.getreviewList(map);

        // 각 리뷰에 대해 관련된 주문 및 주문 상세 정보를 추가
        for (ClientReview review : reviewList) {
            ArrayList<ClientOrder> orderList = clientReviewMapper.getOrderList(map);
            review.setOrderList(orderList);

            for (ClientOrder order : orderList) {
                List<ClientOrderDetail> orderDetailList = clientReviewMapper.getOrderDetailListByOrderKey(order.getOrderKey());
                order.setClientOrderDetails((ArrayList<ClientOrderDetail>) orderDetailList);
            }
        }

        return reviewList;
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

    //사용자키값에 따른 주문목록
    @Override
    public ArrayList<ClientOrder> getAllOrderList(int clientKey, String storeKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("clientKey", clientKey);
        map.put("storeKey", storeKey);

        return clientReviewMapper.getOrderList(map);
    }


}
