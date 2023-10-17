package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("结果模板返回对象")
@Data
public class AchievementsResultTemplateVO {
    @ApiModelProperty(value = "结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "结果设置名称")
    private String resultTemplateName;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "修改人")
    private String updateUserName;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    private Date updateTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "结果模板等级列表")
    private List<ResultTemplateLevelVO> resultTemplateLevelBOList;
}
