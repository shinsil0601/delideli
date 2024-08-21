package kr.co.jhta.app.delideli.user.store.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class StoreInfo {
    private int storeInfoKey;
    private int clientKey;
    private String storeName;
    private String storeAddress;
    private String storeZipcode;
    private String storeAddrDetail;
    private String storePhone;
    private int minOrderAmount;
    private int orderAmount1;
    private int deliveryAmount1;
    private Integer orderAmount2;
    private Integer deliveryAmount2;
    private Integer orderAmount3;
    private Integer deliveryAmount3;
    private String openTime;
    private String closeTime;
    private String storeDetailInfo;
    private String storeOriginInfo;
    private String storeBusinessRegistrationFile;
    private String storeBusinessReportFile;
    private String storeProfileImg;
    private boolean storeAccess;
    private boolean storeDelete;
    private boolean storePause;
    private LocalDateTime storeRegdate;
    private LocalDateTime storeUpdate;
    private String businessStatus;

    private Integer deliveryTime; // 배달 시간 (분 단위)
    private String averageRating; // 평균 리뷰 점수
    private int reviewCount;      // 리뷰 개수
    private Menu firstMenu;       // 첫 번째 메뉴 정보

    // 추가된 필드들
    private String clientName;       // 대표자명
    private String clientPhone;           // 사업장 전화번호
    private String clientEID;  // 사업자 등록번호

    public String getBusinessStatus() {
        if (storePause) {
            return "일시정지";
        }

        LocalTime now = LocalTime.now();
        LocalTime open = LocalTime.parse(this.openTime);
        LocalTime close = LocalTime.parse(this.closeTime);

        if (now.isAfter(open) && now.isBefore(close)) {
            return "영업중";
        } else {
            return "마감";
        }
    }
}
