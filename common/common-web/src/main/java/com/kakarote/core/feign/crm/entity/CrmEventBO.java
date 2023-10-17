package com.kakarote.core.feign.crm.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CrmEventBO {

    private Long userId;

    private Date startTime;

    private Date endTime;

    private Integer expiringDay;

    private Date time;

    private Long companyId;

    @Override
    public String toString() {
        return "CrmEventBO{" +
                "userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", expiringDay=" + expiringDay +
                ", time=" + time +
                ", companyId=" + companyId +
                '}';
    }
}
