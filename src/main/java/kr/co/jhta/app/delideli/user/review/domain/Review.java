package kr.co.jhta.app.delideli.user.review.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class Review {
    private int reviewKey;
    private int userKey;
    private int clientKey;
    private int storeInfoKey;
    private int orderKey;
    private int reviewRating;
    private String reviewDesc;
    private String reviewPhoto1;
    private String reviewPhoto2;
    private Date reviewRegdate;
    private String reviewComment;
    private boolean reportReview;
    private Date commentRegdate;
    private Date commentUpdate;
    private boolean reviewStatus;
}
