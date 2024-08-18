package kr.co.jhta.app.delideli.user.board.service;

import kr.co.jhta.app.delideli.user.board.domain.Comment;

import java.util.List;

public interface UserCommentService {
    //이벤트 댓글 삽입
    void getCommentsByBoardKey(Comment comment);
    //이벤트 댓글 조회
    List<Comment> getCommentAll(int boardKey);
    //이벤트 댓글 수정
    void updateComment(Comment comment);
    //이벤트 댓글 삭제
    void deleteComment(int commentKey);
    //이벤트 댓글 답글삽입
    void insertReplyComment(Comment comment);
}
