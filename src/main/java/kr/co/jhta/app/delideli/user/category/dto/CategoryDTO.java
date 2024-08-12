package kr.co.jhta.app.delideli.user.category.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CategoryDTO {
    private int categoryKey;
    private String categoryName;
    private String categoryImg;
    private LocalDate categoryRegdate;
    private LocalDate categoryUpdate;
}
