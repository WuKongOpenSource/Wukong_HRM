package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @创建人 xuepengfei
 * @创建时间 2022/12/24
 * @描述
 */
@Data
public class ExternalContactSend {

    private Long customerId;

    private String customerName;

    private Long userId;
    private String wxUserId;

    private Long companyId;

    private String corpid;

    private String permanentCode;

    private Long agentId;

    @ApiModelProperty("客户数量")
    private Integer customerNum;
}
