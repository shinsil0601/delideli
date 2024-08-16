package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.common.util.PageUtil;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.domain.Comment;
import kr.co.jhta.app.delideli.user.board.service.BoardService;
import kr.co.jhta.app.delideli.user.board.service.BoardServiceImpl;
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
    @Autowired
    private BoardServiceImpl boardServiceImpl;

    // 공지사항 목록
    @GetMapping("/notice")
    public String notice(Model model,
                         @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                         @RequestParam(name = "page", defaultValue = "1") int page, @AuthenticationPrincipal User user) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
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
    public String detail(@PathVariable int num, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        Board board = boardService.readOneNotice(num);
        model.addAttribute("board", board);
        return "/user/board/noticeDetail";
    }

    // 이벤트 목록
    @GetMapping("/event")
    public String event(Model model, @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                        @RequestParam(name = "page", defaultValue = "1") int page,@AuthenticationPrincipal User user) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
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
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        model.addAttribute("board", board);
        return "/user/board/eventDetail";
    }

    // 댓글 삽입
    @PostMapping("/eventDetail/comment/write")
    public String insertComment(@ModelAttribute Comment comment, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        commentService.getCommentsByBoardKey(comment);
        return "redirect:/user/eventDetail/" + comment.getBoardKey();
    }

    // 댓글 조회 ajax 처리
    @GetMapping("/getCommentList")
    @ResponseBody
    public List<Comment> getCommentList(@RequestParam("boardKey") int boardKey, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
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

    //내문의 목록창이동
    @GetMapping("/myAsk")
    public String myAsk(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        log.info("userKey>>>>>>>>>!!! " + userAccount.getUserKey());
        List<Board> list;

        list = boardService.getMyAskList((long)userAccount.getUserKey());
        model.addAttribute("list", list);

        return "/user/mypage/myAsk";
    }

    //내문의 글작성 창이동
    @GetMapping("/myAskWrite")
    public String myAskWrite(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "/user/mypage/myAskWrite";
    }

    //내문의 글작성
    @PostMapping("/myAskWrite")
    public String myAskWrite(@AuthenticationPrincipal User user, @ModelAttribute Board board) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        board.setUserKey(userAccount.getUserKey());  // Long 타입을 int 타입으로 변환
        boardService.myAskWrite(board);  // Board 객체를 매개변수로 전달

        return "redirect:/user/myAsk";
    }

    //내문의 상세보기
    @GetMapping("/myAskDetail/{boardKey}")
    public String myAskDetail(@AuthenticationPrincipal User user, @PathVariable int boardKey, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        Board board = boardService.myAskDetail(boardKey);
        model.addAttribute("user", userAccount);
        model.addAttribute("board", board);
        return "/user/mypage/myAskDetail";
    }

    // 내문의 글 삭제
    @GetMapping("/myAskDelete/{boardKey}")
    public String deleteMyAsk(@PathVariable int boardKey) {
        boardService.myAskDelete(boardKey);
        return "redirect:/user/myAsk";
    }

}
