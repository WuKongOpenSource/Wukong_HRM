package com.kakarote.hrm.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("考核结果模板列表对象")
@Data
public class ResultTemplatePageVO {

    @ApiModelProperty(value = "结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "结果设置名称")
    private String resultTemplateName;

    @ApiModelProperty(value = "等级设置")
    private String levelSetting;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "修改人")
    private String updateUserName;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "修改日期")
    private Date updateTime;

    @ApiModelProperty(value = "状态：1：正常 0：删除")
    private Integer status;
}
