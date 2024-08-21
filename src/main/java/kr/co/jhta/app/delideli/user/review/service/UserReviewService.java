package kr.co.jhta.app.delideli.user.review.service;

import kr.co.jhta.app.delideli.user.review.domain.Review;

public interface UserReviewService {
    boolean isReviewWritten(int orderKey);

    Review getReviewByOrderKey(int orderKey);

    void saveReview(Review review);

}
