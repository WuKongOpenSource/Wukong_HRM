package com.kakarote.hrm.entity.BO;

import com.kakarote.core.entity.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class QueryAchievementsTemplateBO extends PageEntity {
    @ApiModelProperty("考核模板名称")
    private String templateName;
}
