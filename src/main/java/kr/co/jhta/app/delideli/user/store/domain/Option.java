package kr.co.jhta.app.delideli.user.store.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Option {
    private int optionKey;
    private int optionGroupKey;
    private String optionName;
    private int optionPrice;
    private String optionStatus;
    private Date optionRegdate;
    private Date optionUpdate;
}
