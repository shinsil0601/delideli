package kr.co.jhta.app.delideli.user.dto;

import lombok.*;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CartDTO {
    private int cartKey;
    private int quantity;
    private ArrayList<Integer> selectedOptionKeys;
}
