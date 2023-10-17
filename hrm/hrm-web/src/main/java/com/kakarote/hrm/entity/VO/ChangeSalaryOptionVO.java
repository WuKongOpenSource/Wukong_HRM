package com.kakarote.hrm.entity.VO;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ChangeSalaryOptionVO {
    private String name;

    private Integer code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String value;

    @ApiModelProperty(value = "语言包map")
    private Map<String, String> languageKeyMap;

    public ChangeSalaryOptionVO(String name, Integer code, String value) {
        this.name = name;
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ChangeSalaryOptionVO{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", value='" + value + '\'' +
                '}';
    }
}
