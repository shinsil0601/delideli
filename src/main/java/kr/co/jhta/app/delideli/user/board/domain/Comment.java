package kr.co.jhta.app.delideli.user.board.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Comment {
    private int commentKey;
    private Integer commentParent;
    private String commentContents;
    private LocalDate commentRegdate;
    private LocalDate commentUpdate;
    private LocalDate commentDeldate;
    private int boardKey;
    private int userKey;
    private String userNickname;
}
