package kr.co.jhta.app.delideli.user.coupon.service;

import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;

import java.util.ArrayList;

public interface UserCouponService {

    ArrayList<Coupon> getCouponsByUserKey(int userKey);

    void removeCoupon(int couponKey, int userKey);
}
