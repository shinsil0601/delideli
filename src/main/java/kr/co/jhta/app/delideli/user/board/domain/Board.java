package kr.co.jhta.app.delideli.user.board.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Board {
    private int boardKey;
    private int userKey;
    private String boardType;
    private String boardStatus;
    private String boardProgressStatus;
    private String boardTitle;
    private String boardWrite;
    private String boardContents;
    private String boardThumbnail;
    private String boardComment;
    private int boardCommentStatus;
    private LocalDate boardStartDate;
    private LocalDate boardEndDate;
    private LocalDate boardRegdate;
    private LocalDate boardCommentDate;
    private LocalDate boardUpdate;
    private String fileAttach1;
    private String fileAttach2;
    private String fileAttach3;
    private String fileAttach4;
    private int boardHits;
}
