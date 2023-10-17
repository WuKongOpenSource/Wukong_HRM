package com.kakarote.common.field.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.kakarote.core.common.enums.FieldEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiwei
 * 通用的自定义字段对象
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@ApiModel("自定义字段对象")
public class ModelField implements Serializable {

    public ModelField() {

    }

    public ModelField(String fieldName, String name) {
        this.fieldName = fieldName;
        this.name = name;
    }


    public ModelField(String fieldName, String name, FieldEnum fieldEnum) {
        this.fieldName = fieldName;
        this.name = name;
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
    }

    public ModelField(Long fieldId, String fieldName, String name, FieldEnum fieldEnum, Integer fieldType) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.name = name;
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
        this.fieldType = fieldType;
    }

    public ModelField(String fieldName, FieldEnum fieldEnum) {
        this.fieldName = StrUtil.toCamelCase(fieldName);
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
    }

    public ModelField(String fieldName, FieldEnum fieldEnum, Integer fieldType) {
        this.fieldName = StrUtil.toCamelCase(fieldName);
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
        this.fieldType = fieldType;
    }

    public ModelField(String fieldName, FieldEnum fieldEnum, String name, Integer fieldType) {
        this.fieldName = StrUtil.toCamelCase(fieldName);
        this.type = fieldEnum.getType();
        this.formType = fieldEnum.getFormType();
        this.name = name;
        this.fieldType = fieldType;
    }

    @ApiModelProperty(value = "字段ID")
    @TableId(value = "field_id", type = IdType.ASSIGN_ID)
    private Long fieldId;

    @ApiModelProperty(value = "模块名称")
    private Integer label;

    @ApiModelProperty(value = "自定义字段英文标识")
    private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段类型 1 单行文本 2 多行文本 3 单选 4日期 5 数字 6 小数 7 手机  8 文件 9 多选 10 人员 11 附件 12 部门 13 日期时间 14 邮箱 15客户 16 商机 17 联系人 18 地图 19 产品类型 20 合同 21 回款计划")
    private Integer type;

    @ApiModelProperty(value = "字段类型")
    @TableField(exist = false)
    private String formType;

    @ApiModelProperty(value = "如果类型是选项，此处不能为空，多个选项以，隔开")
    private String options;

    @ApiModelProperty(value = "字段类型 0、自定义 1、系统 2、系统字段但是在自定义字段表 3、冗余字段")
    private Integer fieldType;

    @ApiModelProperty(value = "设置列表")
    @TableField(exist = false)
    private List<Object> setting = new ArrayList<>();

    @ApiModelProperty(value = "默认值")
    private Object defaultValue;

    @ApiModelProperty(value = "value")
    @TableField(exist = false)
    private Object value;

    @ApiModelProperty(value = "字段说明  特别用途 - 明细表格：添加字段说明 | 单选/多选： 标识开启逻辑表单")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "逻辑表单数据")
    private Map<String, Object> optionsData;

}
