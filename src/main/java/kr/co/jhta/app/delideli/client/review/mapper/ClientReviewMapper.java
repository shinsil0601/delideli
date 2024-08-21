package kr.co.jhta.app.delideli.client.review.mapper;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.order.domain.ClientOrderDetail;
import kr.co.jhta.app.delideli.client.review.domain.ClientReview;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClientReviewMapper {
    //사장님 가게에 따른 리뷰
    ArrayList<ClientReview> getreviewList(Map<String, Object> map);
    // 리뷰 신고 처리
    int updateReportReview(int reviewKey);

    ClientReview getReviewByKey(int reviewKey);
    //사용자키값에 따른 주문목록
    ArrayList<ClientOrder> getOrderList(Map<String, Object> map);
    // 각 주문에 연결된 주문 상세 리스트 가져오기
    List<ClientOrderDetail> getOrderDetailListByOrderKey(int orderKey);

    void updateComment(Map<String, Object> params);

    void addNewComment(Map<String, Object> params);
}
