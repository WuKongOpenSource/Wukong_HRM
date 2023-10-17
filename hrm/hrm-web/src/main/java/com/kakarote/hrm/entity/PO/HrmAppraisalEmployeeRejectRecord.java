package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工绩效考核-驳回记录表
 * </p>
 *
 * @author zyl
 * @since 2022-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_reject_record")
@ApiModel(value = "HrmAppraisalEmployeeRejectRecord对象", description = "员工绩效考核-驳回记录表")
public class HrmAppraisalEmployeeRejectRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "驳回主键di")
    private Long rejectId;

    @ApiModelProperty(value = "员工绩效id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "驳回阶段 1：评分阶段")
    private Integer rejectStage;

    @ApiModelProperty(value = "驳回记录id")
    private Long rejectRecordId;

    @ApiModelProperty(value = "驳回人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "处理状态:0未处理 1已处理")
    private Integer status;

}
