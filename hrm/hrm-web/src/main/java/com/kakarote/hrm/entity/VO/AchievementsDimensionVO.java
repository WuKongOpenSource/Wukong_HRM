package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("绩效考核维度")
public class AchievementsDimensionVO {

    @ApiModelProperty(value = "维度Id")
    private Long dimensionId;

    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "指标类型")
    private Integer quotaType;

    @ApiModelProperty(value = "维度权重")
    private Double dimensionWeight;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "允许员工填写1：是 0：否")
    private Boolean isAllowEdit;

    @ApiModelProperty(value = "模板id")
    private Long templateId;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "修改人")
    private String updateUserName;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    private Date updateTime;

    @ApiModelProperty(value = "考核维度指标列表")
    private List<AchievementsQuotaVO> achievementsQuotaVOList;
}
