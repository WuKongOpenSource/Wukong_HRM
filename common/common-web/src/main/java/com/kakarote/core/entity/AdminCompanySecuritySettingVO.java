package com.kakarote.core.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 企业安全设置表
 * </p>
 *
 * @author yangzenghui
 * @since 2023-08-28
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "AdminCompanySecuritySetting对象", description = "企业安全设置表")
public class AdminCompanySecuritySettingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long configId;

    @ApiModelProperty("规则类型(1.密码规则,2.登出规则）")
    private Integer configType;

    @ApiModelProperty("密码有效期(2147483647(永不过期),30,60,90,120,180,365)天")
    private Integer pwdValidPeriod;

    @ApiModelProperty("强制密码历史(0,1,2,3,4,5,6,7,8,9,10)次")
    private Integer enforcePwdHistory;

    @ApiModelProperty("最小密码强度(6~16)位,默认为6位")
    private Integer minimumPwdLen;

    @ApiModelProperty("字符要求(0.不限 ,1.必须包含字母+数字组合 , 2.必须包含大写字母+小写字母+数字组合,3.必须包含字母+特殊字符+数字组合,4.必须包含大写字母+小写字母+特殊字符+数字组合)")
    private Integer pwdCharRequire;

    @ApiModelProperty("密码输入错误次数(2147483647(不限) ,3,4,5,6)次")
    private Integer pwdErrorNum;

    @ApiModelProperty("修改密码登出规则(1.退出登录其他设备,2.不退出其他登录设备)")
    private Integer changePwdRule;

    @ApiModelProperty("Web页最长空闲时间(值)")
    private Integer maximumIdleTime;

    @ApiModelProperty("Web页最长空闲时间(单位):1.天,2.小时,3.分钟")
    private Integer maximumIdleTimeUnit;

    @ApiModelProperty("移动端最长免登录时间(值)")
    private Integer maximumLoginFreeTime;

    @ApiModelProperty("移动端最长免登录时间(单位):1.天,2.小时,3.分钟")
    private Integer maximumLoginFreeTimeUnit;

    @ApiModelProperty("是否允许多设备登录 1.是,2.否")
    private Integer multDeviceLogin;


}