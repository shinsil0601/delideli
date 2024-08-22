package kr.co.jhta.app.delideli.user.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.common.util.PageUtil;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.board.domain.Comment;
import kr.co.jhta.app.delideli.user.board.service.UserBoardService;
import kr.co.jhta.app.delideli.user.board.service.UserBoardServiceImpl;
import kr.co.jhta.app.delideli.user.board.service.UserCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserBoardController {

    @Autowired
    private final UserBoardService userBoardService;
    @Autowired
    private final UserCommentService userCommentService;
    @Autowired
    private final UserService userService;
    @Autowired
    private UserBoardServiceImpl boardServiceImpl;

    // 공지사항 목록
    @GetMapping("/notice")
    public String notice(Model model,
                         @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
                         @AuthenticationPrincipal User user, HttpServletResponse response) {
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }

        int totalNumber;
        List<Board> list;

        if (keyword.equals("none")) {
            totalNumber = userBoardService.getTotalNotice();
            list = userBoardService.getBoardList(pageSize, (page - 1) * pageSize);
        } else {
            totalNumber = userBoardService.getTotalKeyword(keyword);
            list = userBoardService.getAllKeyword(pageSize, (page - 1) * pageSize, keyword);
            model.addAttribute("keyword", keyword);
        }

        // 페이지네이션 정보 계산
        int totalPages = (int) Math.ceil((double) totalNumber / pageSize);
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("list", list);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);

        return "user/board/notice.list";
    }

    // 공지사항 상세보기
    @GetMapping("/detail/{num}")
    public String detail(@PathVariable int num, @AuthenticationPrincipal User user, Model model, HttpServletResponse response) {
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }
      
        //조회수업뎃
        userBoardService.updateHitNotice(num);
        Board board = userBoardService.readOneNotice(num);
        model.addAttribute("board", board);
        return "user/board/notice.view";
    }

    // 이벤트 목록
    @GetMapping("/event")
    public String event(Model model,
                        @RequestParam(name = "keyword", defaultValue = "none") String keyword,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
                        @AuthenticationPrincipal User user,
                        HttpServletResponse response) {
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }

        int totalNumber;
        List<Board> list;

        if (keyword.equals("none")) {
            totalNumber = userBoardService.getTotalEvent();
            list = userBoardService.getEventList(pageSize, (page - 1) * pageSize);
        } else {
            totalNumber = userBoardService.getTotalKeywordEvent(keyword);
            list = userBoardService.getAllKeywordEvent(pageSize, (page - 1) * pageSize, keyword);
            model.addAttribute("keyword", keyword);
        }

        // 페이지네이션 정보 계산
        int totalPages = (int) Math.ceil((double) totalNumber / pageSize);
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("list", list);
        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);

        return "user/board/event.list";
    }

    // 이벤트 상세보기
    @GetMapping("/eventDetail/{num}")
    public String eventDetail(@AuthenticationPrincipal User user, @PathVariable int num, Model model, HttpServletResponse response) {
        //조회수업뎃
        userBoardService.updateHitEvent(num);
        Board board = userBoardService.readOneEvent(num);
        if (user != null) {
            boolean hasUserRole = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            if (!hasUserRole) {
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JWT", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {
                UserAccount userAccount = userService.findUserById(user.getUsername());
                model.addAttribute("user", userAccount);
            }
        }
        model.addAttribute("board", board);
        return "user/board/event.view";
    }

    // 댓글 삽입
    @PostMapping("/eventDetail/comment/write")
    public String insertComment(@ModelAttribute Comment comment, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        userCommentService.getCommentsByBoardKey(comment);
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
        return userCommentService.getCommentAll(boardKey);
    }

    // 댓글 수정 ajax 처리
    @PostMapping("/editComment")
    @ResponseBody
    public String editComment(@RequestParam("commentKey") int commentKey,
                              @RequestParam("commentContents") String commentContents) {
        Comment comment = new Comment();
        comment.setCommentKey(commentKey);
        comment.setCommentContents(commentContents);
        userCommentService.updateComment(comment);
        return "success";
    }

    // 댓글 삭제 ajax 처리
    @PostMapping("/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentKey") int commentKey) {
        userCommentService.deleteComment(commentKey);
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
        userCommentService.insertReplyComment(comment);
        return "success";
    }

    //내문의 목록창이동
    @GetMapping("/myAsk")
    public String myAsk(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        //log.info("userKey>>>>>>>>>!!! " + userAccount.getUserKey());
        List<Board> list = userBoardService.getMyAskList(userAccount.getUserKey());
        model.addAttribute("list", list);
        model.addAttribute("active", "myAsk");

        return "user/mypage/myAsk";
    }

    //내문의 글작성 창이동
    @GetMapping("/myAskWrite")
    public String myAskWrite(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/myAskWrite";
    }

    //내문의 글작성
    @PostMapping("/myAskWrite")
    public String myAskWrite(@AuthenticationPrincipal User user, @ModelAttribute Board board) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        board.setUserKey(userAccount.getUserKey());
        userBoardService.myAskWrite(board);  // Board 객체를 매개변수로 전달

        return "redirect:/user/myAsk";
    }

    //내문의 상세보기
    @GetMapping("/myAskDetail/{boardKey}")
    public String myAskDetail(@AuthenticationPrincipal User user, @PathVariable int boardKey, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        Board board = userBoardService.myAskDetail(boardKey);
        model.addAttribute("user", userAccount);
        model.addAttribute("board", board);
        return "user/mypage/myAskDetail";
    }

    // 내문의 글 삭제
    @GetMapping("/myAskDelete/{boardKey}")
    public String deleteMyAsk(@PathVariable int boardKey) {
        userBoardService.myAskDelete(boardKey);
        return "redirect:/user/myAsk";
    }

    // 페이지네이션 정보를 맵핑하는 메서드
    private Map<String, Object> createPaginationMap(int currentPage, int totalPages) {
        Map<String, Object> map = new HashMap<>();
        map.put("prev", currentPage > 1);
        map.put("next", currentPage < totalPages);
        map.put("startPageNo", 1); // 필요에 따라 조정 가능
        map.put("endPageNo", totalPages); // 필요에 따라 조정 가능
        return map;
    }
}
