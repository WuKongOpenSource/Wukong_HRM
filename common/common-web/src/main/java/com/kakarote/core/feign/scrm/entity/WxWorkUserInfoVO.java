package com.kakarote.core.feign.scrm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LDH
 * @version 1.0
 * @description: 企业微信用户信息数据传输对象
 * @date 2022/6/7 14:03
 */
@Data
@ApiModel("企业微信用户信息对象")
public class WxWorkUserInfoVO {

    @ApiModelProperty("企业微信userId")
    private String wxWorkUserId;

    @ApiModelProperty("企业微信头像")
    private String avatar;

    @ApiModelProperty("企业微信手机号")
    private String mobile;
}
