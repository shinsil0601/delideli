package kr.co.jhta.app.delideli.client.store.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientStoreCategory {
    private int storeCategoryKey;
    private int storeInfoKey;
    private int categoryKey;
    private LocalDateTime storeCategoryRegdate;
    private LocalDateTime storeCategoryUpdate;
}
