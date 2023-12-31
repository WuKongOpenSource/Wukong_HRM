package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工每月参保项目表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_insurance_month_emp_project_record")
@ApiModel(value = "HrmInsuranceMonthEmpProjectRecord对象", description = "员工每月参保项目表")
public class HrmInsuranceMonthEmpProjectRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "参保项目记录id")
    @TableId(value = "emp_project_record_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long empProjectRecordId;

    @ApiModelProperty(value = "参保员工记录id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long iEmpRecordId;

    @ApiModelProperty(value = "项目id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;

    @ApiModelProperty(value = "1 养老保险基数 2 医疗保险基数 3 失业保险基数 4 工伤保险基数 5 生育保险基数 6 补充大病医疗保险 7 补充养老保险 8 公积金")
    private Integer type;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "默认基数")
    private BigDecimal defaultAmount;

    @ApiModelProperty(value = "公司比例")
    private BigDecimal corporateProportion;

    @ApiModelProperty(value = "个人比例")
    private BigDecimal personalProportion;

    @ApiModelProperty(value = "公司缴纳金额")
    private BigDecimal corporateAmount;

    @ApiModelProperty(value = "个人缴纳金额")
    private BigDecimal personalAmount;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
