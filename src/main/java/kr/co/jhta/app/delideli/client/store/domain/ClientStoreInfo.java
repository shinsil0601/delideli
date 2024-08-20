package kr.co.jhta.app.delideli.client.store.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
public class ClientStoreInfo {
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

    // 포맷된 시간 필드를 추가
    private String formattedOpenTime;
    private String formattedCloseTime;

    // 영업상태 추가 필드
    private String businessStatus;

    // 메서드 추가
    public void formatTimes() {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (openTime != null && !openTime.isEmpty()) {
            this.formattedOpenTime = LocalTime.parse(openTime, inputFormatter).format(outputFormatter);
        }
        if (closeTime != null && !closeTime.isEmpty()) {
            this.formattedCloseTime = LocalTime.parse(closeTime, inputFormatter).format(outputFormatter);
        }
    }

    // 영업 상태 계산 및 시간 포맷팅 메서드
    public void calculateBusinessStatus() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a hh시");

        LocalTime open = LocalTime.parse(openTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime close = LocalTime.parse(closeTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

        // 포맷팅된 시간을 저장
        formattedOpenTime = open.format(timeFormatter);
        formattedCloseTime = close.format(timeFormatter);

        // 영업 상태 계산
        if (storePause) {
            businessStatus = "일시정지";
        } else {
            LocalTime now = LocalTime.now();
            if (now.isAfter(open) && now.isBefore(close)) {
                businessStatus = "영업중";
            } else {
                businessStatus = "마감";
            }
        }
    }
}
