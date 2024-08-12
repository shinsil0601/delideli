package kr.co.jhta.app.delideli.user.main.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class StoreCategoryDTO {
    private int storeCategoryKey;
    private int storeInfoKey;
    private int categoryKey;
    private LocalDate storeCategoryRegdate;
    private LocalDate storeCategoryUpdate;
}
