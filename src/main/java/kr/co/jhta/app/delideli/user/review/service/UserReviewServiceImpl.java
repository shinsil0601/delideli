package kr.co.jhta.app.delideli.user.review.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.review.mapper.UserReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserReviewServiceImpl implements UserReviewService {


    @Autowired
    private UserReviewMapper userReviewMapper;

    @Override
    public boolean isReviewWritten(int orderKey) {
        return userReviewMapper.countByOrderKey(orderKey) > 0;
    }

    @Override
    public Review getReviewByOrderKey(int orderKey) {
        return userReviewMapper.findReviewByOrderKey(orderKey);
    }

    @Override
    public void saveReview(Review review) {
        userReviewMapper.insertReview(review);
    }
}
