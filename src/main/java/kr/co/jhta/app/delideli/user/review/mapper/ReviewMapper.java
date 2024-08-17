package kr.co.jhta.app.delideli.user.review.mapper;

import kr.co.jhta.app.delideli.user.review.domain.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ReviewMapper {

    Double getAverageRatingForStore(int storeInfoKey);

    int getReviewCountForStore(int storeInfoKey);

    ArrayList<Review> getReviewListByStore(int storeInfoKey);

    int countByOrderKey(int orderKey);

    Review findReviewByOrderKey(int orderKey);

    void insertReview(Review review);
}
