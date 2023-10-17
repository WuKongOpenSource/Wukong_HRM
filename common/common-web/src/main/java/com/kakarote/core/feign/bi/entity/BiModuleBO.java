package com.kakarote.core.feign.bi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用模块表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Module对象", description="应用模块表")
public class BiModuleBO implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "应用ID")
    private Long applicationId;

    @ApiModelProperty(value = "主字段ID")
    private Long mainFieldId;

    @ApiModelProperty(value = "模块图标")
    private String icon;

    @ApiModelProperty(value = "图标颜色")
    private String iconColor;

    @ApiModelProperty(value = "字段排序")
    private Integer sort;

    @ApiModelProperty(value = "索引名")
    private String indexName;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "关联的模块")
    private Long relateModuleId;

    @ApiModelProperty(value = "流程管理员")
    private String manageUserId;

    @ApiModelProperty(value = "已激活")
    private Boolean isActive;

    @ApiModelProperty(value = "0 停用 1 正常 2 草稿")
    private Integer status;

    @ApiModelProperty(value = "模块类型 1 无代码模块 2 自定义bi模块 3 自用系统模块 ")
    private Integer moduleType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "公司ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

}
