package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @创建人 xuepengfei
 * @创建时间 2022/12/24
 * @描述
 */
@Data
public class ExternalContact {

    @ApiModelProperty(value = "外部联系人的userid")
    private String externalUserid;

    @ApiModelProperty(value = "外部联系人的名称")
    private String name;

    @ApiModelProperty(value = "外部联系人的职位，如果外部企业或用户选择隐藏职位，则不返回，仅当联系人类型是企业微信用户时有此字段")
    private String position;

    @ApiModelProperty(value = "外部联系人头像，代开发自建应用需要管理员授权才可以获取，第三方不可获取，上游企业不可获取下游企业客户该字段")
    private String avatar;

    @ApiModelProperty(value = "外部联系人所在企业的简称，仅当联系人类型是企业微信用户时有此字段")
    private String corpName;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性。第三方不可获取，上游企业不可获取下游企业客户该字段，返回值为0，表示未定义")
    private Integer gender;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识（微信unionid），通过此字段企业可将外部联系人与公众号/小程序用户关联起来。仅当联系人类型是微信用户，且企业绑定了微信开发者ID有此字段。查看绑定方法。第三方不可获取，上游企业不可获取下游企业客户的unionid字段")
    private String unionid;
}
