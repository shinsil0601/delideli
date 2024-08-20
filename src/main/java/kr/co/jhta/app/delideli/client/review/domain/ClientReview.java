package kr.co.jhta.app.delideli.client.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientReview {
    private int reviewKey;
    private int userKey;
    private int clientKey;
    private int storeInfoKey;
    private int orderKey;
    private double reviewRating;
    private String reviewDesc;
    private String reviewPhoto1;
    private String reviewPhoto2;
    private Date reviewRegdate;
    private String reviewComment;
    private boolean reportReview;
    private Date commentRegdate;
    private Date commentUpdate;
    private boolean reviewStatus;
    private Date mnmtRegdate;
    private String userId;
}
