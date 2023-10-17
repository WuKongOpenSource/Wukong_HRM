package com.kakarote.hrm.entity.BO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetSlipRemarksBO {
    private List<Long> ids;

    private String remarks;

    @Override
    public String toString() {
        return "SetSlipRemarksBO{" +
                "ids=" + ids +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
