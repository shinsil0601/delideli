package kr.co.jhta.app.delideli.user.board.service;

import kr.co.jhta.app.delideli.user.board.domain.Comment;
import kr.co.jhta.app.delideli.user.board.mapper.UserBoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {

    private final UserBoardMapper userBoardMapper;

    //이벤트 댓글 삽입
    @Override
    public void getCommentsByBoardKey(Comment comment) {
        userBoardMapper.addComment(comment);
    }

    //이벤트 댓글 조회
    @Override
    public List<Comment> getCommentAll(int boardKey) {
        List<Comment> list = userBoardMapper.getCommentAll(boardKey);
        return list;
    }

    //이벤트 댓글 수정
    @Override
    public void updateComment(Comment comment) {
        userBoardMapper.updateComment(comment);
    }

    //이벤트 댓글 삭제
    @Override
    public void deleteComment(int commentKey) {
        // 자식 댓글 먼저 삭제
        userBoardMapper.deleteChildComments(commentKey);
        // 부모 댓글 삭제
        userBoardMapper.deleteComment(commentKey);
    }

    //이벤트 댓글 답글삽입
    @Override
    public void insertReplyComment(Comment comment) {
        userBoardMapper.insertReplyComment(comment);
    }
}
