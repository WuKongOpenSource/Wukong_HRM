package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @创建人 xuepengfei
 * @创建时间 2023/2/25
 * @描述
 */
@Data
@ApiModel("钉钉云平台注册")
public class AdminCloudDingtalkCO {

    @ApiModelProperty("部门列表")
    private List<AdminDeptCO> deptList;

    @ApiModelProperty("用户列表")
    private List<AdminUserCO> userList;

    @ApiModelProperty("注册用户信息")
    private AdminUserCO adminUserCO;

    @ApiModelProperty("钉钉企业信息")
    private AdminCompanyDingCO adminCompanyDingCO;

}
