package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateInsuranceSchemeBO {
    @ApiModelProperty("员工ids")
    private List<Long> employeeIds;

    @ApiModelProperty(value = "参保方案id")
    private Long schemeId;

    @Override
    public String toString() {
        return "UpdateInsuranceSchemeBO{" +
                "employeeIds=" + employeeIds +
                ", schemeId=" + schemeId +
                '}';
    }
}
