package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @创建人 xuepengfei
 * @创建时间 2022/12/24
 * @描述
 */
@Data
public class FollowInfo {

    @ApiModelProperty(value = "添加了此外部联系人的企业成员userid")
    private String userid;

    @ApiModelProperty(value = "该成员对此外部联系人的备注")
    private String remark;

    @ApiModelProperty(value = "该成员对此外部联系人的描述")
    private String description;

    @ApiModelProperty(value = "添加此外部联系人的时间")
    private Long createtime;

    @ApiModelProperty(value = "发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid")
    private String operUserid;

    @ApiModelProperty(value = "该成员对此客户备注的手机号码，代开发自建应用需要管理员授权才可以获取，第三方不可获取，上游企业不可获取下游企业客户该字段")
    private List<String> remarkMobiles;

    @ApiModelProperty(value = "该成员对此微信客户备注的企业名称（仅微信客户有该字段）")
    private String remarkCorpName;
}
