package com.kakarote.core.feign.scrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author LDH
 * @version 1.0
 * @description: 企业微信公司对象
 * @date 2021/12/8 15:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_wx_corp")
@ApiModel(value="WxCorp对象", description="企微会话存储基本信息")
public class WxWorkConfig implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("公司id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

    @ApiModelProperty(value = "企业微信公司id")
    private String corpId;

    @ApiModelProperty(value = "自建应用id")
    private String appId;

    @ApiModelProperty(value = "自建应用secret")
    private String secretApp;

    @ApiModelProperty(value = "会话存储secret")
    private String secretSession;

    @ApiModelProperty(value = "客户sercet")
    private String secretCustomer;

    @ApiModelProperty(value = "用户sercet")
    private String secretUser;

}
