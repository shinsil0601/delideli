package kr.co.jhta.app.delideli.user.board.mapper;

import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface UserBoardMapper {
    // 공지사항 목록
    List<Board> getAll(HashMap<String, Integer> map);
    // 공지사항 검색키워드 리스트
    List<Board> getAllKeyword(HashMap<String, Object> map);
    // 공지사항 총갯수
    int getTotalNotice();
    // 공지사항 상세보기
    Board getOneNotice(int num);
    // 공지사항 검색키워드 총갯수
    int getTotalKeyword(String keyword);

    // 이벤트 목록
    List<Board> getEventList(HashMap<String, Integer> map);
    // 이벤트 상세보기
    Board readOneEvent(int num);
    // 이벤트 댓글 삽입
    void addComment(Comment comment);
    // 이벤트 댓글 목록
    List<Comment> getCommentAll(int boardKey);
    // 이벤트 총갯수
    int getTotalEvent();
    // 이벤트 검색키워드 총갯수
    int getTotalKeywordEvent(String keyword);
    // 이벤트 검색 목록
    List<Board> getAllKeywordEvent(HashMap<String, Object> map);
    // 이벤트 댓글 수정
    void updateComment(Comment comment);
    // 이벤트 댓글 삭제
    void deleteComment(int commentKey);
    // 이벤트 대댓글 삭제
    void deleteChildComments(int commentKey);
    // 이벤트 댓글 답글삽입
    void insertReplyComment(Comment comment);

    //내문의 글목록
    List<Board> getMyAskList(int userKey);
    //내문의 글작성
    void myAskWrite(Board board);
    //내문의 상세보기
    Board myAskDetail(int boardKey);
    //내문의 글삭제
    void myAskDelete(int boardKey);

    //이벤트 조회수 업데이트
    void updateHitEvent(int num);

    //공지사항 상세보기 조회수업데이트
    void updateHitNotice(int num);
    //공지사항 목록 리스트(최대 4개)
    ArrayList<Board> noticeList();
    //이벤트 배너 이미지(최대 3개)
    ArrayList<Board> eventList();
}
