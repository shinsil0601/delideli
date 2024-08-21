package kr.co.jhta.app.delideli.client.review.domain;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientReview {
    private int reviewKey;
    private int userKey;
    private int clientKey;
    private int storeInfoKey;
    private int orderKey;
    private double reviewRating;
    private String reviewDesc;
    private String reviewPhoto1;
    private String reviewPhoto2;
    private LocalDateTime reviewRegdate;
    private String reviewComment;
    private boolean reportReview;
    private LocalDateTime commentRegdate;
    private LocalDateTime commentUpdate;
    private boolean reviewStatus;
    private LocalDateTime mnmtRegdate;
    private String userId;
    private String userNickname;

    // 새로운 필드: 해당 리뷰에 연관된 주문 리스트
    private List<ClientOrder> orderList;
}
