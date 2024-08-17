package kr.co.jhta.app.delideli.user.review.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import kr.co.jhta.app.delideli.user.review.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService{


    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public boolean isReviewWritten(int orderKey) {
        return reviewMapper.countByOrderKey(orderKey) > 0;
    }

    @Override
    public Review getReviewByOrderKey(int orderKey) {
        return reviewMapper.findReviewByOrderKey(orderKey);
    }

    @Override
    public void saveReview(Review review) {
        reviewMapper.insertReview(review);
    }
}
