package kr.co.jhta.app.delideli.user.coupon.service;

import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;
import kr.co.jhta.app.delideli.user.coupon.mapper.CouponMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    public ArrayList<Coupon> getCouponsByUserKey(int userKey) {
        return couponMapper.getCouponsByUserKey(userKey);
    }

    @Override
    public void removeCoupon(int couponKey, int userKey) {
        couponMapper.deleteCouponByKeyAndUser(couponKey, userKey);
    }
}
