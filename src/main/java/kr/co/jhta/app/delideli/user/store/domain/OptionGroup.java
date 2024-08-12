package kr.co.jhta.app.delideli.user.store.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
public class OptionGroup {
    private int optionGroupKey;
    private int menuKey;
    private String optionGroupName;
    private boolean required;
    private int maxSelectOption;
    private Date optionGroupRegdate;
    private Date optionGroupUpdate;

    private ArrayList<Option> options;
}
