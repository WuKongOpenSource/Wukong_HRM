package com.kakarote.core.feign.oa.entity;

import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.entity.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhiwei
 * crm通用搜索对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "高级筛选BO", description = "高级筛选表")
@ToString
public class OaSearchBO extends PageEntity implements Serializable {

    @ApiModelProperty(value = "搜索条件")
    private String search;

    @ApiModelProperty(value = "公海ID")
    private Long poolId;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

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
    private Object[] searchAfterKey;

    @ApiModelProperty(value = "用于判定待办审批是否带上审批记录id")
    private Boolean examineRecordId;
    /**
     * 设置afterKey
     * @param objects data
     */
    public void searchAfter(Object[] objects) {
        searchAfterKey = objects;
    }

}
