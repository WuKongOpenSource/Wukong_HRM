package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 云平台公司配置表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_admin_company")
@ApiModel(value="AdminCompany对象", description="云平台公司配置表")
public class AdminCompany implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "公司ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

    @ApiModelProperty(value = "登录账号")
    private String phone;

    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @ApiModelProperty(value = "企业优惠吗")
    private String companyDiscount;

    @ApiModelProperty(value = "企业注册来源")
    private String companySource;

    @ApiModelProperty(value = "企业logo")
    private String companyLogo;

    @ApiModelProperty(value = "企业状态 1 正常 0 禁用")
    private Integer companyStatus;

    @ApiModelProperty(value = "公司超级管理员ID")
    private String companyManage;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;


}
