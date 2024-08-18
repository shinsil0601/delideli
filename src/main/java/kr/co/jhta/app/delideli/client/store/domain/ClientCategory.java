package kr.co.jhta.app.delideli.client.store.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientCategory {
    private int categoryKey;
    private String categoryName;
    private String categoryImg;
    private LocalDate categoryRegdate;
    private LocalDate categoryUpdate;
}
