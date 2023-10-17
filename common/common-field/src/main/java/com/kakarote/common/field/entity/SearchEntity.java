package com.kakarote.common.field.entity;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.entity.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiwei
 * 业务通用搜索对象
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "高级筛选entity")
public class SearchEntity extends PageEntity {

    @ApiModelProperty(value = "搜索条件")
    private String search;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序字段 1 倒序 2 正序")
    private Integer order;

    @ApiModelProperty(value = "searchAfter搜索所需key")
    @Setter(AccessLevel.NONE)
    private List<FieldValue> searchAfterKey;

    @ApiModelProperty(value = "高级筛选列表")
    private List<Search> searchList;

    @ApiModelProperty("额外的筛选数据")
    private Map<String, Object> extraMap;

    @ApiModelProperty(value = "搜索的字段")
    private List<String> fetchFieldNameList = new ArrayList<>();

    @ApiModelProperty(value = "是否设置searchAfter")
    private Boolean appendSearchAfter = false;

    public Map<String, Object> getExtraMap() {
        if (extraMap == null) {
            extraMap = new HashMap<>(2, 0.75F);
        }
        return extraMap;
    }

    /**
     * 设置afterKey
     *
     * @param searchAfterKey data
     */
    public void searchAfter(List<FieldValue> searchAfterKey) {
        this.searchAfterKey = searchAfterKey;
    }

    public List<Search> getSearchList() {
        if (searchList == null) {
            searchList = new ArrayList<>();
        }
        return searchList;
    }
}
