package kr.co.jhta.app.delideli.client.order.domain;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOrder {
    private int orderKey;
    private int userKey;
    private int storeInfoKey;
    private String orderNo;
    private String address;
    private String orderMethod;
    private int uorderPrice;
    private String uorderPayment;
    private String orderEstimatedTime;
    private String riderDesc;
    private String shopDesc;
    private LocalDateTime orderRegdate;
    private LocalDateTime orderUpdate;
    private String formattedOrderDate;
    private String storeName;
    private String statusMessage;
    private String expectedArrivalTime;
    private int remainingDays;
    private Review review;
    private StoreInfo storeInfo;

    private ArrayList<ClientOrderDetail> clientOrderDetails;
}
