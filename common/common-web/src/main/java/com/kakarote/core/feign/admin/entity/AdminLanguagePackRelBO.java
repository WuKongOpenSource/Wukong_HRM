package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 语言包表
 * </p>
 *
 * @author zmj
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AdminLanguagePackRelBO对象", description="语言包表")
public class AdminLanguagePackRelBO implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "语言包关联id")
    private Long languagePackRelId;

    @ApiModelProperty(value = "关系名称")
    private String languagePackRelName;

    @ApiModelProperty(value = "字段名")
    private String languagePackRelField;

    @ApiModelProperty(value = "语言包key值")
    private String languagePackRelKey;

    @ApiModelProperty(value = "扩展信息key")
    private String expendDataKey;

    @ApiModelProperty(value = "扩展参数信息")
    private String expendData;

    @ApiModelProperty(value = "模块名称")
    private String moduleId;

    @ApiModelProperty(value = "label")
    private Integer label;

    @ApiModelProperty(value = "数据id")
    private Long dataId;

    @ApiModelProperty(value = "批次id")
    private String batchId;

    @ApiModelProperty(value = "是否系统默认(0：系统，1：用户)")
    private Integer sysDefault;

    @ApiModelProperty(value = "语言包类型(1：pc，2：app)")
    private Integer languagePackType;

    @ApiModelProperty(value = "信息类型（详见FieldFlagEnum）")
    private Integer fieldFlag;

}
