package com.kakarote.hrm.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
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
 *
 * </p>
 *
 * @author zyl
 * @since 2022-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_employee_appeal_record")
@ApiModel(value = "HrmAppraisalEmployeeAppealRecord对象", description = "")
public class HrmAppraisalEmployeeAppealRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appeal_record_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "申诉记录id")
    private Long appealRecordId;

    @ApiModelProperty(value = "员工绩效考核id")
    private Long appraisalEmployeeId;

    @ApiModelProperty(value = "申诉评分节点")
    private Long appraisalStageId;

    @ApiModelProperty(value = "处理状态:0未处理 1已处理")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
