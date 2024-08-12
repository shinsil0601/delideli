package kr.co.jhta.app.delideli.user.category.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class StoreInfoDTO {
    private int storeInfoKey;
    private int clientKey;
    private String storeName;
    private String storeAddress;
    private String storeZipcode;
    private String storeAddrsDetail;
    private String storePhone;
    private int minOrderAmount;
    private int orderAmount1;
    private int deliveryAmount1;
    private Integer orderAmount2;
    private Integer deliveryAmount2;
    private Integer orderAmount3;
    private Integer deliveryAmount3;
    private LocalDate openTime;
    private LocalDate closeTime;
    private String storeDetailInfo;
    private String storeOriginInfo;
    private String storeBusinessRegistrationFile;
    private String storeBusinessReportFile;
    private String storeProfileImg;
    private boolean storeAccess;
    private boolean storeDelete;
    private LocalDate storeRegdate;
    private LocalDate storeUpdate;
}
