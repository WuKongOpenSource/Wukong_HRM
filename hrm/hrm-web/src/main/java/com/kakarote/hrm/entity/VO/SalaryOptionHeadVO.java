package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryOptionHeadVO {

    @ApiModelProperty("薪资项code")
    private Integer code;

    @ApiModelProperty("薪资项名称")
    private String name;

    @ApiModelProperty("薪资项名称")
    private Integer isFixed;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    public SalaryOptionHeadVO(Integer code, String name, Integer isFixed) {
        this.code = code;
        this.name = name;
        this.isFixed = isFixed;
    }

    @Override
    public String toString() {
        return "SalaryOptionHeadVO{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", isFixed=" + isFixed +
                ", value='" + value + '\'' +
                '}';
    }
}
