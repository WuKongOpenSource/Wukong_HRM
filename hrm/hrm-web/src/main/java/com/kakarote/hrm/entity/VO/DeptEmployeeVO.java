package com.kakarote.hrm.entity.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeptEmployeeVO {


    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "父级ID 顶级部门为0")
    private Long parentId;

    @ApiModelProperty(value = "1 公司 2 部门")
    private Integer deptType;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "部门编码")
    private String code;

    @ApiModelProperty("在职人数")
    private Integer allNum;

    @ApiModelProperty("是否有下级部门 0 否 1 是")
    private Integer hasChildren;

    @ApiModelProperty("当前部门在职人数")
    private Integer currentNum;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DeptEmployeeVO> children;

    @Override
    public String toString() {
        return "DeptEmployeeVO{" +
                "deptId=" + deptId +
                ", parentId=" + parentId +
                ", deptType=" + deptType +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", allNum=" + allNum +
                ", hasChildren=" + hasChildren +
                ", currentNum=" + currentNum +
                '}';
    }
}
