package com.kakarote.core.feign.crm.entity;

import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.entity.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("待办事项模块查询BO")
public class CrmBackLogBO extends PageEntity {

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("isSub")
    private Integer isSub;

    @ApiModelProperty(value = "高级筛选列表")
    private List<Search> data = new ArrayList<>();

    @ApiModelProperty(value = "OA类型")
    private Long categoryId;
}
