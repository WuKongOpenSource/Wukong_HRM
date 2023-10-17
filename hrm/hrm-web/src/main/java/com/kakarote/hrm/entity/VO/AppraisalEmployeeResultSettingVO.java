package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "结果设置返回对象")
public class AppraisalEmployeeResultSettingVO {

    @ApiModelProperty(value = "是否同步到薪资")
    private Boolean syncToSalary;

    @ApiModelProperty(value = "引用的结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "参与计薪月份")
    private String paidForMonth;

    @ApiModelProperty(value = "结果设置等级列表")
    private List<AppraisalPlanResultSettingVO> appraisalPlanResultSettingLevelList;
}
