package kr.co.jhta.app.delideli.user.like.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private int likeKey;
    private int userKey;
    private int storeInfoKey;
    private int likeStatus; // 0: 취소, 1: 찜하기
    private LocalDateTime likeRedate; // 등록일
    private LocalDateTime likeUpdate; // 업데이트 날짜
}
