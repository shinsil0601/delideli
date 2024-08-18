package kr.co.jhta.app.delideli.user.coupon.service;

import kr.co.jhta.app.delideli.user.coupon.domain.Coupon;
import kr.co.jhta.app.delideli.user.coupon.mapper.UserCouponMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponMapper userCouponMapper;

    public UserCouponServiceImpl(UserCouponMapper userCouponMapper) {
        this.userCouponMapper = userCouponMapper;
    }

    @Override
    public ArrayList<Coupon> getCouponsByUserKey(int userKey) {
        return userCouponMapper.getCouponsByUserKey(userKey);
    }

    @Override
    public void removeCoupon(int couponKey, int userKey) {
        userCouponMapper.deleteCouponByKeyAndUser(couponKey, userKey);
    }

    @Override
    public ArrayList<Coupon> getCouponsByUserKeyWithPaging(int userKey, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userCouponMapper.getCouponsByUserKeyWithPaging(userKey, offset, pageSize);
    }

    @Override
    public int getTotalCouponsByUserKey(int userKey) {
        return userCouponMapper.getTotalCouponsByUserKey(userKey);
    }
}
