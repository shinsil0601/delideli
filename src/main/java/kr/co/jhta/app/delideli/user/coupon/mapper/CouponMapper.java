package kr.co.jhta.app.delideli.user.coupon.mapper;

import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface CouponMapper {
    // 특정 유저의 쿠폰 리스트를 가져오기
    ArrayList<Coupon> getCouponsByUserKey(int userKey);
}
