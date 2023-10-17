package com.kakarote.core.feign.crm.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ReUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.enums.DataAuthEnum;
import com.kakarote.core.common.enums.DateFilterEnum;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.exception.CrmException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiwei
 * bi参数
 */
@ApiModel("bi查询相关参数")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BiEntityParams extends PageEntity implements Serializable {

    @ApiModelProperty("数据权限类型")
    @JsonProperty("dataType")
    private DataAuthEnum dataType;

    @ApiModelProperty(value = "日期筛选类型")
    @JsonProperty("dateFilter")
    private DateFilterEnum dateFilter;

    @ApiModelProperty("开始时间，日期筛选类型等于‘custom’时有效")
    private Date startDate;

    @ApiModelProperty("结束，日期筛选类型等于‘custom’时有效")
    private Date endDate;

    @ApiModelProperty("用户ID数组，当dateType=0时有效")
    private List<Long> userList;

    @ApiModelProperty("部门ID数组，当dateType=0时有效")
    private List<Long> deptList;

    @ApiModelProperty(value = "是否展示子级部门 0不需要 1 需要")
    private Integer isNeedChild;

    @ApiModelProperty("type，用途不唯一")
    private Integer type;

    @ApiModelProperty("排序字段")
    private String sortField;

    @ApiModelProperty("操作行为")
    private List<List<String>> behavior = new ArrayList<>();

    @ApiModelProperty("操作对象")
    private String title;

    @ApiModelProperty("操作详情")
    private String detailName;


    /**
     * 当数据筛选不存在时，按照自定义处理
     * @return DataAuthEnum
     */
    public DataAuthEnum getDataType() {
        return dataType != null ? dataType : DataAuthEnum.CUSTOM;
    }

    /**
     * 当日期筛选不存在时按照本月处理
     * @return DateFilterEnum
     */
    public DateFilterEnum getDateFilter() {
        return dateFilter != null ? dateFilter : DateFilterEnum.MONTH;
    }
    public DateFilterEnum getOriginDateFilter() {
        return dateFilter;
    }

    /**
     * 将当前参数签名生成KEY，用于做redis等二次缓存
     *
     * @return key
     */
    public final String generateKey() {
        Map<String, Object> objectMap = BeanUtil.beanToMap(this, false, true);
        if (UserUtil.getUser() != null) {
            objectMap.put("userId", UserUtil.getUserId());
        }
        return MapUtil.sortJoin(objectMap, StrPool.AT, StrPool.COLON, false);
    }

    public void setSortField(String sortField) {
        //防止sql注入，排序字段只能是字母，数字，下划线
        if (!ReUtil.isMatch(PatternPool.GENERAL, sortField)) {
            throw new CrmException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        this.sortField = sortField;
    }
    public Integer getIsNeedChild() {
        if (isNeedChild == null) {
            isNeedChild =0;
        }
        return isNeedChild;
    }
}
