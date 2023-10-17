package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmployeeArchivesFieldVO {

    @ApiModelProperty(value = "主键ID")
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "标签分组 * 1 员工个人信息 2 通讯信息")
    private Integer labelGroup;

    @ApiModelProperty(value = "是否员工可见 0 否 1 是")
    private Integer isEmployeeVisible;

    @ApiModelProperty(value = "是否员工可修改 0 否 1 是")
    private Integer isEmployeeUpdate;


    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    @Override
    public String toString() {
        return "EmployeeArchivesFieldVO{" +
                "fieldId=" + fieldId +
                ", name='" + name + '\'' +
                ", labelGroup=" + labelGroup +
                ", isEmployeeVisible=" + isEmployeeVisible +
                ", isEmployeeUpdate=" + isEmployeeUpdate +
                '}';
    }
}
