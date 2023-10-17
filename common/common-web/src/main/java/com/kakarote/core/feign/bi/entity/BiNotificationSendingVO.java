package com.kakarote.core.feign.bi.entity;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @创建人 xuepengfei
 * @创建时间 2023/3/17
 * @描述
 */
@ApiOperation("企业微信通知发送提醒")
@Data
public class BiNotificationSendingVO {

    private String wxUserId;
    private Long companyId;
    private Long userId;
    private Long followLeads;
    private Long followCustomer;
    private Long todayLeads;
    private Long todayCustomer;
    private Long todayBusiness;
}
