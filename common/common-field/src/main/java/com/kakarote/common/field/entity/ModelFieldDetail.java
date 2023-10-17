package com.kakarote.common.field.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author zhangzhiwei
 * 自定义字段详细信息,因为完整信息在大部分情况是不需要的
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@ApiModel("自定义字段详细信息对象")
public class ModelFieldDetail extends ModelField {


    @ApiModelProperty(value = "输入提示")
    private String inputTips;

    @ApiModelProperty(value = "最大长度")
    private Integer maxLength;

    @ApiModelProperty(value = "默认值")
    private Object defaultValue;

    @ApiModelProperty(value = "是否唯一 1 是 0 否")
    private Integer isUnique;

    @ApiModelProperty(value = "是否必填 1 是 0 否")
    private Integer isNull;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;

    @ApiModelProperty(value = "样式百分比  1 100%  2 75%  3 50%  4 25%")
    private Integer stylePercent;

    @ApiModelProperty(value = "限制的最大数值")
    private String maxNumRestrict;

    @ApiModelProperty(value = "限制的最小数值")
    private String minNumRestrict;

    @ApiModelProperty(value = "精度，允许的最大小数位/地图精度/明细表格、逻辑表单展示方式")
    private Integer precisions;

    @ApiModelProperty(value = "表单辅助id，前端生成")
    private Long formAssistId;

    @ApiModelProperty(value = "表单定位 坐标格式： 1,1")
    private String formPosition;

    @ApiModelProperty(value = "逻辑表单数据")
    @TableField(exist = false)
    private Map<String, Object> optionsData;

    @ApiModelProperty(value = "是否可以删除修改 0 改删 1 改 2 删 3 无")
    private Integer operating;

    @ApiModelProperty(value = "是否隐藏  0不隐藏 1隐藏")
    private Integer isHidden;

}
