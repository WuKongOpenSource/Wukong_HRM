package com.kakarote.core.feign.admin.entity;

import lombok.Data;

import java.util.List;

/**
 * @创建人 xuepengfei
 * @创建时间 2022/12/16
 * @描述
 */
@Data
public class CpCustomerInfo {

    private ExternalContact externalContact;

    private FollowInfo followInfo;

    private List<FollowUser> followUser;

}
