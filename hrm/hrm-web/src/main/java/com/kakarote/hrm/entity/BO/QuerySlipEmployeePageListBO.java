package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuerySlipEmployeePageListBO extends PageEntity {

    @ApiModelProperty(value = "员工名称")
    private String employeeName;

    @ApiModelProperty("部门名称")
    private Long deptId;

    @ApiModelProperty(value = "发送状态 0 未发送 1 已发送")
    private Integer sendStatus;

    @Override
    public String toString() {
        return "QuerySlipEmployeePageListBO{" +
                "employeeName='" + employeeName + '\'' +
                ", deptId=" + deptId +
                ", sendStatus=" + sendStatus +
                '}';
    }
}
