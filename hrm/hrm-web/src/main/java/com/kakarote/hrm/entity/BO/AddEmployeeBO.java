package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 员工表
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@ApiModel(value = "员工添加对象", description = "员工表")
public class AddEmployeeBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工Id")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    @NotBlank(message = "员工名称不能为空")
    private String employeeName;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "证件类型 1 身份证 2 港澳通行证 3 台湾通行证 4 护照 5 其他")
    private Integer idType;

    @ApiModelProperty(value = "证件号码")
    private String idNumber;

    @ApiModelProperty(value = "性别 1 男 2 女")
    private Integer sex;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "入职时间")
    private LocalDate entryTime;

    @ApiModelProperty(value = "试用期 0 无试用期")
    private Integer probation;

    @ApiModelProperty(value = "转正日期")
    private LocalDate becomeTime;

    @ApiModelProperty(value = "工号")
    private String jobNumber;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "直属上级ID")
    private Long parentId;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty(value = "工作城市")
    private String workCity;

    @ApiModelProperty(value = "聘用形式 1 正式 2 非正式")
    private Integer employmentForms;

    @ApiModelProperty(value = "员工状态 1正式 2试用  3实习 4兼职 5劳务 6顾问 7返聘 8外包 9待离职 10已离职")
    private Integer status;

    @ApiModelProperty(value = "候选人id")
    private Long candidateId;

    @ApiModelProperty("入职状态 1已入职 2待入职")
    private Integer entryStatus;

}
