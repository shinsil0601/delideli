package kr.co.jhta.app.delideli.user.board.service;

import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    // 공지사항 목록
    @Override
    public List<Board> getBoardList(int startNo, int endNo) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        List<Board> list = boardMapper.getAll(map);
        return list;
    }

    //공지사항 검색키워드 리스트
    @Override
    public List<Board> getAllKeyword(int startNo, int endNo, String keyword) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        List<Board> list = boardMapper.getAllKeyword(map);
        return list;
    }

    //공지사항 총갯수
    @Override
    public int getTotalNotice() {
        return boardMapper.getTotalNotice();
    }

    //공지사항 검색키워드 총갯수
    @Override
    public int getTotalKeyword(String keyword) {
        return boardMapper.getTotalKeyword(keyword);
    }

    //공지사항 상세보기
    @Override
    public Board readOneNotice(int num) {
        Board board = boardMapper.getOneNotice(num);
        return board;
    }

    //이벤트 목록
    @Override
    public List<Board> getEventList(int startNo, int endNo) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        List<Board> list = boardMapper.getEventList(map);
        return list;
    }

    //이벤트 상세보기
    @Override
    public Board readOneEvent(int num) {
        Board board = boardMapper.readOneEvent(num);
        return board;
    }

    //이벤트 총갯수
    @Override
    public int getTotalEvent() {
        return boardMapper.getTotalEvent();
    }

    //이벤트 검색키워드 총갯수
    @Override
    public int getTotalKeywordEvent(String keyword) {
        return boardMapper.getTotalKeywordEvent(keyword);
    }

    //이벤트 검색 목록
    @Override
    public List<Board> getAllKeywordEvent(int startNo, int endNo, String keyword) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("startNo", startNo);
        map.put("endNo", endNo);
        List<Board> list = boardMapper.getAllKeywordEvent(map);
        return list;
    }
}
