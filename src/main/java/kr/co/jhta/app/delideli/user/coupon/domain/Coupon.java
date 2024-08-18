package kr.co.jhta.app.delideli.user.coupon.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private int couponKey;
    private int userKey;
    private String couponName;
    private int couponPrice;
    private String couponUseGrade;
    private LocalDateTime couponStartDate;
    private LocalDateTime couponEndDate;
    private LocalDateTime couponRegdate;
    private LocalDateTime couponUpdate;
}
