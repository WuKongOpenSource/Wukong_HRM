package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("结果模板保存实体类")
public class ResultTemplateBO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "结果模板id")
    private Long resultTemplateId;

    @ApiModelProperty(value = "结果设置名称")
    private String resultTemplateName;

    private Long createUserId;

    @ApiModelProperty(value = "等级列表")
    private List<ResultTemplateLevelBO> resultTemplateLevelBOList;
}
