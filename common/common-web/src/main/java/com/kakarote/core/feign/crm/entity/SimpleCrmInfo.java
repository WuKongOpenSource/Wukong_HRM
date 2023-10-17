package com.kakarote.core.feign.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author JiaS
 * @date 2020/12/25
 */
@Data
public class SimpleCrmInfo implements Serializable {

    private static final long serialVersionUID=1L;



    private String name;

    private Long examineRecordId;

    private Long createUserId;

    private Integer examineType;

    private String category;

    private Long categoryId;

    private Long categoryCiteId;

    private String categoryCiteName;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime realInvoiceDate;

    private SimpleUser createUser;

    @ApiModelProperty("审批状态")
    private Integer examineStatus;
}
