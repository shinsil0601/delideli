package kr.co.jhta.app.delideli.client.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOrderDetail {
    private int orderDetailKey;
    private int orderKey;
    private String menuName;
    private String optionName;
    private int quantity;
}
