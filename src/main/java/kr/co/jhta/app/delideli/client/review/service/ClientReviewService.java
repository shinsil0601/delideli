package kr.co.jhta.app.delideli.client.review.service;

import kr.co.jhta.app.delideli.client.review.domain.ClientReview;

import java.util.ArrayList;

public interface ClientReviewService {
    //사장님 가게에 따른 리뷰
    ArrayList<ClientReview> getAllReview(int clientKey, String storeKey);
    // 리뷰 신고 처리
    boolean reportReview(int reviewKey);
}
