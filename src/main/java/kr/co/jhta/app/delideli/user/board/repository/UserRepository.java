package kr.co.jhta.app.delideli.user.board.repository;

import kr.co.jhta.app.delideli.user.board.dto.BoardDTO;
import kr.co.jhta.app.delideli.user.board.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface UserRepository {
    //공지사항 목록
    List<BoardDTO> getAll(HashMap<String, Integer> map);
    //공지사항 검색키워드 리스트
    List<BoardDTO> getAllKeyword(HashMap<String,Object> map);
    //공지사항 총갯수
    int getTotalNotice();
    //공지사항 상세보기
    BoardDTO getOneNotice(int num);
    //공지사항 검색키워드 총갯수
    int getTotalKeyword(String keyword);

    //이벤트 목록
    List<BoardDTO> getEventList(HashMap<String, Integer> map);
    //이벤트 상세보기
    BoardDTO readOneEvent(int num);
    //이벤트 댓글 삽입
    void addComment(CommentDTO commentDTO);
    //이벤트 댓글 목록
    List<CommentDTO> getCommentAll(int boardKey);
    //이벤트 총갯수
    int getTotalEvent();
    //이벤트 검색키워드 총갯수
    int getTotalKeywordEvent(String keyword);
    //이벤트 검색 목록
    List<BoardDTO> getAllKeywordEvent(HashMap<String, Object> map);
    //이벤트 댓글 수정
    void updateComment(CommentDTO commentDTO);
    //이벤트 댓글 삭제
    void deleteComment(int commentKey);
    //이벤트 댓글 답글삽입
    void insertReplyComment(CommentDTO commentDTO);
}
