package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryAchievementsResultTemplateBO extends PageEntity {

    @ApiModelProperty("考核结果模板名称")
    private String resultTemplateName;
}
