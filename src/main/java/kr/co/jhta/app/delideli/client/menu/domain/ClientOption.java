package kr.co.jhta.app.delideli.client.menu.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOption {
    private int optionKey;
    private int optionGroupKey;
    private String optionName;
    private int optionPrice;
    private String optionStatus;
    private Date optionRegdate;
    private Date optionUpdate;
}
