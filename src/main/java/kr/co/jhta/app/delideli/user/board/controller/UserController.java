package kr.co.jhta.app.delideli.user.board.controller;

import kr.co.jhta.app.delideli.user.board.dto.BoardDTO;
import kr.co.jhta.app.delideli.user.board.dto.CommentDTO;
import kr.co.jhta.app.delideli.user.board.service.BoardService;
import kr.co.jhta.app.delideli.user.board.service.CommentService;
import kr.co.jhta.app.delideli.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final BoardService boardService;
    private final CommentService commentService;
    //목록으로
    @GetMapping(value = {"/", "/index"})
    public String home() {
        return "index";
    }

    //공지사항 목록
    @GetMapping("/user/notice")
    public String notice(Model model,
                         @RequestParam(name = "keyword", defaultValue = "none")String keyword,
                         @RequestParam(name = "page", defaultValue = "1")int page) {
        //총게시물수
        int totalNumber = 0;
        //페이지당 게시물수
        int recordPerPage = 5;
        Map<String, Object> map = null;
        List<BoardDTO> list = null;

        if (keyword.equals("none")) {
            //전체 게시물 조회
            totalNumber = boardService.getTotalNotice();
            System.out.println("전체게시물"+ totalNumber);
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            //        PageUtil.getPageData(총게시물수, 페이지번호, 레코드갯수);

            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");

            list = boardService.getBoardList(startNo, endNo);

        }else {
            //키워드 검색
            System.out.println("키워드게시물"+ totalNumber);
            totalNumber = boardService.getTotalKeyword(keyword);
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");

            list = boardService.getAllKeyword(startNo, endNo, keyword);
            model.addAttribute("keyword", keyword);
        }
        model.addAttribute("list", list);
        model.addAttribute("map", map);

        return "user/board/userNotice";
    }

    //공지사항 상세보기
    @GetMapping("/user/detail/{num}")
    public String detail(@PathVariable int num, Model model) {
        BoardDTO dto = boardService.readOneNotice(num);
        model.addAttribute("dto", dto);
        return "user/board/userNoticeDetail";
    }

    //이벤트 목록
    @GetMapping("/user/event")
    public String event(Model model, @RequestParam(name = "keyword", defaultValue = "none")String keyword,
                        @RequestParam(name = "page", defaultValue = "1")int page) {
        //총게시물수
        int totalNumber = 0;
        //페이지당 게시물수
        int recordPerPage = 5;
        Map<String, Object> map = null;
        List<BoardDTO> list = null;

        if (keyword.equals("none")) {
            //전체 게시물 조회
            totalNumber = boardService.getTotalEvent();
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            //        PageUtil.getPageData(총게시물수, 페이지번호, 레코드갯수);

            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            //System.out.println("시작번호: "+ startNo +", 끝번호: "+ endNo);

            list = boardService.getEventList(startNo, endNo);
        }else {
            //키워드 검색
            totalNumber = boardService.getTotalKeywordEvent(keyword);
            map = PageUtil.getPageData(totalNumber, recordPerPage, page);
            int startNo = (int) map.get("startNo");
            int endNo = (int) map.get("endNo");
            //System.out.println("시작번호: "+ startNo +", 끝번호: "+ endNo);

            list = boardService.getAllKeywordEvent(startNo, endNo, keyword);
            model.addAttribute("keyword", keyword);
        }
            model.addAttribute("list", list);
            model.addAttribute("map", map);

        return "user/board/userEvent";
    }

    //이벤트 상세보기
    @GetMapping("/user/eventDetail/{num}")
    public String eventDetail(@PathVariable int num, Model model) {
        BoardDTO dto = boardService.readOneEvent(num);

        model.addAttribute("dto", dto);

        return "user/board/userEventDetail";
    }

    //댓글 삽입
    @PostMapping("/user/eventDetail/comment/write")
    public String insertComment(@ModelAttribute CommentDTO commentDTO){
        commentService.getCommentsByBoardKey(commentDTO);
        return "redirect:/user/eventDetail/"+commentDTO.getBoardKey();
    }

    //댓글 조회 ajax처리
    @GetMapping("/user/getCommentList")
    @ResponseBody
    public List<CommentDTO> getCommentList(@RequestParam("boardKey")int boardKey) {
        List<CommentDTO> list = commentService.getCommentAll(boardKey);
       /* for (CommentDTO comment : list) {
            System.out.println("댓글조회컨트롤러"+ comment.getCommentContents());
        }*/

        return list;
    }

    // 댓글 수정 ajax처리
    @PostMapping("/user/editComment")
    @ResponseBody
    public String editComment(@RequestParam("commentKey") int commentKey,
                              @RequestParam("commentContents") String commentContents) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentKey(commentKey);
        commentDTO.setCommentContents(commentContents);
        commentService.updateComment(commentDTO);
        return "success";  // 성공 응답
    }

    // 댓글 삭제 ajax처리
    @PostMapping("/user/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("commentKey") int commentKey) {
        commentService.deleteComment(commentKey);
        return "success"; // 성공 응답
    }

    //댓글 답글삽입
    @PostMapping("/user/insertReplyComment")
    @ResponseBody
    public String replyComment(@RequestParam("commentContents") String commentContents,
                               @RequestParam("commentParent") int commentParent,
                               @RequestParam("boardKey") int boardKey,
                               @RequestParam("userKey") int userKey) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentContents(commentContents);
        commentDTO.setCommentParent(commentParent);
        commentDTO.setBoardKey(boardKey);
        commentDTO.setUserKey(userKey);
        commentService.insertReplyComment(commentDTO);
        System.out.println("대댓내용: "+ commentContents);
        return "success"; // 성공 응답
    }
}
