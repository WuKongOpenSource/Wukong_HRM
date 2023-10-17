package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author LDH
 * @version 1.0
 * @description: TODO
 * @date 2023/2/24 10:59
 */
@Data
@Accessors(chain = true)
@TableName("wk_admin_company_ding")
@ApiModel(value="AdminCompanyDing对象", description="钉钉企业配置表")
@NoArgsConstructor
public class AdminCompanyDingCO implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("授权企业的CorpId")
    private String corpid;

    @ApiModelProperty("微应用ID")
    private String agentid;

    @ApiModelProperty("邀请码，只有自己邀请的企业才会返回邀请码，可用该邀请码统计不同渠道的拉新，否则值为空字符串")
    private String inviteCode;

    @ApiModelProperty("企业所属行业")
    private String industry;

    @ApiModelProperty("授权方企业名称")
    private String corpName;

    @ApiModelProperty("序列号")
    private String licenseCode;

    @ApiModelProperty("渠道码")
    private String authChannel;

    @ApiModelProperty("渠道类型。为了避免渠道码重复，可与渠道码共同确认渠道。可能为空，非空时当前只有满天星类型，值为STAR_ACTIVITY")
    private String authChannelType;

    @ApiModelProperty("企业认证等级：0：未认证1：高级认证2：中级认证3：初级认证")
    private String authLevel;

    @ApiModelProperty("永久授权码")
    private String permanentCode;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;

}
