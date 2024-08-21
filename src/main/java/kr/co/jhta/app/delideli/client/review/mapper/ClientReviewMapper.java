package kr.co.jhta.app.delideli.client.review.mapper;

import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface ClientReviewMapper {
    // 리뷰 신고 처리
    int updateReportReview(int reviewKey);

    ClientReview getReviewByKey(int reviewKey);

    void updateComment(Map<String, Object> params);

    void addNewComment(Map<String, Object> params);

    ArrayList<ClientReview> getAllReviewWithPaging(Map<String, Object> map);

    int getTotalReviewsByStoreKey(Map<String, Object> map);
}
