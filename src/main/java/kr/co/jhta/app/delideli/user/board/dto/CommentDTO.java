package kr.co.jhta.app.delideli.user.board.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CommentDTO {
    private int commentKey;
    private Integer commentParent;
    private String commentContents;
    private LocalDate commentRegdate;
    private LocalDate commentUpdate;
    private LocalDate commentDeldate;
    private int boardKey;
    private int userKey;
    private String userNickname;
}
