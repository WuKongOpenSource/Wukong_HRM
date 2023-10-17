package com.kakarote.core.feign.admin.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "AdminApplyOrder订单对象", description = "应用申请订单表")
public class AdminApplyOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单中的应用名称")
    private String name;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("关闭订单的时间")
    private LocalDateTime closeTime;

    @ApiModelProperty("年份")
    private int year;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty("订单状态1支付成功，2结果确认中，3待支付，4已关闭，5支付超时关闭")
    private Integer status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("支付方式1微信2支付宝3其它")
    private Integer payWay;

    @ApiModelProperty("下单的用户id")
    private Long userId;

    @ApiModelProperty("微信native支付codeurl")
    private String wxCodeUrl;

    @ApiModelProperty("支付宝网页支付codeurl")
    private String aliCodeUrl;

    @ApiModelProperty("备注")
    private String msg;

    @ApiModelProperty("企业ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @ApiModelProperty("是否已申请合同，1已申请，0未申请")
    private Integer contract;

    @ApiModelProperty("是否已申请发票，1已申请，0未申请")
    private Integer invoice;

    @ApiModelProperty("下单用户手机号")
    private String mobile;

}

