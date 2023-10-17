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
 * 绩效考核审核记录
 * </p>
 *
 * @author zyl
 * @since 2022-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_hrm_appraisal_action_record")
@ApiModel(value = "HrmAppraisalActionRecord对象", description = "绩效考核审核记录")
public class HrmAppraisalActionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "ip地址")
    private String ipAddress;

    @ApiModelProperty(value = "操作类型 1 员工 2 招聘管理 3 候选人 4 绩效管理 5：KPI考核计划")
    private Integer type;

    @ApiModelProperty(value = "操作对象id")
    private Long typeId;

    @ApiModelProperty(value = "操作行为 1 新建 2 编辑 3 删除 4 转正 5 调岗 6 晋升 7 降级 8 转全职员工 9 离职 10 参保方案")
    private Integer behavior;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "翻译内容")
    private String transContent;

    @ApiModelProperty(value = "附件批次id")
    private String batchId;

    @ApiModelProperty(value = "操作人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "操作人名称")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "周几")
    private String week;

    @ApiModelProperty(value = "更新人id")
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
