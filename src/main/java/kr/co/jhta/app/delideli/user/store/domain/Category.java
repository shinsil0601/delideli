package kr.co.jhta.app.delideli.user.store.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Category {
    private int categoryKey;
    private String categoryName;
    private String categoryImg;
    private LocalDate categoryRegdate;
    private LocalDate categoryUpdate;
}
