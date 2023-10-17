package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.core.common.RangeValidated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工证书
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_employee_certificate")
@ApiModel(value = "HrmEmployeeCertificate对象", description = "员工证书")
@RangeValidated
public class HrmEmployeeCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "certificate_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long certificateId;

    @ApiModelProperty(value = "员工id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    @ApiModelProperty(value = "证书名称")
    private String certificateName;

    @ApiModelProperty(value = "证书级别")
    private String certificateLevel;

    @ApiModelProperty(value = "证书编号")
    private String certificateNum;

    @ApiModelProperty(value = "有效起始日期")
    private LocalDate startTime;

    @ApiModelProperty(value = "有效结束日期")
    private LocalDate endTime;

    @ApiModelProperty(value = "发证机构")
    private String issuingAuthority;

    @ApiModelProperty(value = "发证日期")
    private LocalDate issuingTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
