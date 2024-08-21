package kr.co.jhta.app.delideli.client.review.service;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.review.domain.ClientReview;

import java.util.ArrayList;

public interface ClientReviewService {
    // 리뷰 신고 처리
    boolean reportReview(int reviewKey);

    void updateComment(int reviewKey, String updatedComment);

    void addNewComment(int reviewKey, String newComment);

    ArrayList<ClientReview> getAllReviewWithPaging(int clientKey, String storeKey, int page, int pageSize);

    int getTotalReviewsByStoreKey(int clientKey, String storeKey);
}
