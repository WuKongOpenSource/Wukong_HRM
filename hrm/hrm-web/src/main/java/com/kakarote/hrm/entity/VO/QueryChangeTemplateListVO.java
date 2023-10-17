package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryChangeTemplateListVO {
    private Long id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "记录类型 1 定薪 2 调薪")
    private Integer recordType;

    @ApiModelProperty(value = "是否默认 0 否 1 是")
    private Integer isDefault;

    @ApiModelProperty("定薪项")
    private List<ChangeSalaryOptionVO> value;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    @Override
    public String toString() {
        return "QueryChangeTemplateListVO{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", recordType=" + recordType +
                ", isDefault=" + isDefault +
                ", value=" + value +
                '}';
    }
}
