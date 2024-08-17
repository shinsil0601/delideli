package kr.co.jhta.app.delideli.user.board.service;


import kr.co.jhta.app.delideli.user.board.domain.Board;

import java.util.List;

public interface UserBoardService {
    // 공지사항 목록
    List<Board> getBoardList(int startNo, int endNo);
    //공지사항 검색 목록
    List<Board> getAllKeyword(int startNo, int endNo, String keyword);
  
    //공지사항 총갯수
    int getTotalNotice();
    //공지사항 검색키워드 총갯수
    int getTotalKeyword(String keyword);
    //공지사항 상세보기
    Board readOneNotice(int num);
  
    //이벤트 목록
    List<Board> getEventList(int startNo, int endNo);
    //이벤트 상세보기
    Board readOneEvent(int num);

    //이벤트 총갯수
    int getTotalEvent();
    //이벤트 검색키워드 총갯수
    int getTotalKeywordEvent(String keyword);
    //이벤트 검색 목록
    List<Board> getAllKeywordEvent(int startNo, int endNo, String keyword);

    //내문의 목록
    List<Board> getMyAskList(int userKey);
    //내문의 글작성
    void myAskWrite(Board board);
    //내문의 상세보기
    Board myAskDetail(int boardKey);
    //내문의 글삭제
    void myAskDelete(int boardKey);
}
