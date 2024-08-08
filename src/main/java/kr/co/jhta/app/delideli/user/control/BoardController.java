package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.common.util.PageUtil;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.domain.Comment;
import kr.co.jhta.app.delideli.user.board.service.BoardService;
import kr.co.jhta.app.delideli.user.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @Autowired
    private final BoardService boardService;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final UserService userService;

    // 공지사항 목록
    @GetMapping("/notice")
    public String notice(Model model,
                         @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                         @RequestParam(name = "page", defaultValue = "1") int page) {
        int totalNumber = 0;
        int recordPerPage = 5;
        Map<String, Object> map;
        List<Board> list;

        if (keyword.equals("none")) {
            totalNumber = boardService.getTotalNotice();
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            list = boardService.getBoardList(startNo, endNo);
        } else {
            totalNumber = boardService.getTotalKeyword(keyword);
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            list = boardService.getAllKeyword(startNo, endNo, keyword);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("list", list);
        model.addAttribute("map", map);

        return "/user/board/notice";
    }

    // 공지사항 상세보기
    @GetMapping("/detail/{num}")
    public String detail(@PathVariable int num, Model model) {
        Board board = boardService.readOneNotice(num);
        model.addAttribute("board", board);
        return "/user/board/noticeDetail";
    }

    // 이벤트 목록
    @GetMapping("/event")
    public String event(Model model, @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                        @RequestParam(name = "page", defaultValue = "1") int page) {
        int totalNumber = 0;
        int recordPerPage = 5;
        Map<String, Object> map;
        List<Board> list;

        if (keyword.equals("none")) {
            totalNumber = boardService.getTotalEvent();
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            list = boardService.getEventList(startNo, endNo);
        } else {
            totalNumber = boardService.getTotalKeywordEvent(keyword);
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            list = boardService.getAllKeywordEvent(startNo, endNo, keyword);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("list", list);
        model.addAttribute("map", map);

        return "/user/board/event";
    }

    // 이벤트 상세보기
    @GetMapping("/eventDetail/{num}")
    public String eventDetail(@AuthenticationPrincipal User user, @PathVariable int num, Model model) {
        Board board = boardService.readOneEvent(num);
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        model.addAttribute("board", board);
        return "/user/board/eventDetail";
    }

    // 댓글 삽입
    @PostMapping("/eventDetail/comment/write")
    public String insertComment(@ModelAttribute Comment comment) {
        commentService.getCommentsByBoardKey(comment);
        return "redirect:/user/eventDetail/" + comment.getBoardKey();
    }

    // 댓글 조회 ajax 처리
    @GetMapping("/getCommentList")
    @ResponseBody
    public List<Comment> getCommentList(@RequestParam("boardKey") int boardKey) {
        return commentService.getCommentAll(boardKey);
    }

    // 댓글 수정 ajax 처리
    @PostMapping("/editComment")
    @ResponseBody
    public String editComment(@RequestParam("commentKey") int commentKey,
                              @RequestParam("commentContents") String commentContents) {
        Comment comment = new Comment();
        comment.setCommentKey(commentKey);
        comment.setCommentContents(commentContents);
        commentService.updateComment(comment);
        return "success";
    }

    // 댓글 삭제 ajax 처리
    @PostMapping("/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentKey") int commentKey) {
        commentService.deleteComment(commentKey);
        return "success";
    }

    // 댓글 답글 삽입
    @PostMapping("/insertReplyComment")
    @ResponseBody
    public String replyComment(@RequestParam("commentContents") String commentContents,
                               @RequestParam("commentParent") int commentParent,
                               @RequestParam("boardKey") int boardKey,
                               @RequestParam("userKey") int userKey) {
        Comment comment = new Comment();
        comment.setCommentContents(commentContents);
        comment.setCommentParent(commentParent);
        comment.setBoardKey(boardKey);
        comment.setUserKey(userKey);
        commentService.insertReplyComment(comment);
        return "success";
    }
}
