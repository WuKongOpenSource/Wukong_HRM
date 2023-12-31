package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryRecordListBO {

    @ApiModelProperty(value = "操作类型 1 员工  2 招聘管理 3 候选人 4 绩效管理 5:KPI考核")
    private Integer type;

    @ApiModelProperty(value = "操作对象id")
    private Long typeId;

    @Override
    public String toString() {
        return "QueryRecordListBO{" +
                "type=" + type +
                ", typeId=" + typeId +
                '}';
    }
}
