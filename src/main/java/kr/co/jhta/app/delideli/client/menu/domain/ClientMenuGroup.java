package kr.co.jhta.app.delideli.client.menu.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientMenuGroup {
    private int menuGroupKey;
    private String menuGroupName;
    private LocalDateTime menuGroupRegdate;
    private LocalDateTime menuGroupUpdate;
}
