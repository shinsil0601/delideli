package kr.co.jhta.app.delideli.client.review.mapper;

import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface ClientReviewMapper {
    //사장님 가게에 따른 리뷰
    ArrayList<ClientReview> getreviewList(Map<String, Object> map);
    // 리뷰 신고 처리
    int updateReportReview(int reviewKey);

    ClientReview getReviewByKey(int reviewKey);
}
