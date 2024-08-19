package kr.co.jhta.app.delideli.client.menu.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOptionGroup {
    private int optionGroupKey;
    private int menuKey;
    private String optionGroupName;
    private boolean required;
    private int minSelectOption;
    private int maxSelectOption;
    private Date optionGroupRegdate;
    private Date optionGroupUpdate;

    private ArrayList<ClientOption> options;
}
