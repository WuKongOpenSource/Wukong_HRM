package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuerySendRecordListBO extends PageEntity {

    private Integer year;

    private Integer month;

    @Override
    public String toString() {
        return "QuerySendRecordListBO{" +
                "year=" + year +
                ", month=" + month +
                '}';
    }
}
