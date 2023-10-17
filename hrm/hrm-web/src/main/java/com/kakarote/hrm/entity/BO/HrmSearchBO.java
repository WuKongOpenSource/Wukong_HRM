package com.kakarote.hrm.entity.BO;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.entity.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "高级筛选BO", description = "高级筛选表")
@ToString
public class HrmSearchBO extends PageEntity implements Serializable {

    @ApiModelProperty(value = "搜索条件")
    private String search;

    @ApiModelProperty(value = "type")
    private Integer label;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;

    @ApiModelProperty(value = "高级筛选列表")
    private List<Search> searchList = new ArrayList<>();

    @ApiModelProperty(value = "searchAfter搜索所需key")
    @Setter(AccessLevel.NONE)
    private List<FieldValue> searchAfterKey;

    /**
     * 设置afterKey
     *
     * @param objects data
     */
    public void searchAfter(List<FieldValue> objects) {
        searchAfterKey = objects;
    }

}
