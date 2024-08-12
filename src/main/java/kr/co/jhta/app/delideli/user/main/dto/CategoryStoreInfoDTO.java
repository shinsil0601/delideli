package kr.co.jhta.app.delideli.user.main.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CategoryStoreInfoDTO {
    private int categoryKey;
    private String categoryName;
    private String categoryImg;
    private LocalDate categoryRegdate;
    private LocalDate categoryUpdate;
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
    // 필요한 추가 필드들
}
