package com.kakarote.core.feign.examine.entity;


import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("crm审核高级配置对象")
public class ExamineAdvancedConfigVO {

    @ApiModelProperty(value = "高级配置id")
    private Long advancedConfigId;

    @ApiModelProperty(value = "审批配置id")
    private Long examineId;

    @ApiModelProperty(value = "节点为空处理类型异常处理类型1：自动同意2：转交给指定人员处理")
    private Integer nodeHandleType;

    @ApiModelProperty(value = "处理人")
    private List<SimpleUser> nodeHandleUser;

    @ApiModelProperty(value = "当同一个审批人重复审批同一单据时处理类型1:仅首个节点需审批，其余自动同意2:仅连续审批时自动同意3:每个节点都需要审批")
    private Integer repeatHandleType;

    @ApiModelProperty(value ="审批被拒后重新提交类型1：跳过审批流已通过的层级，返回拒绝的层级2：返回审批流初始层级" )
    private Integer rejectHandleType;

    @ApiModelProperty(value = "修改权限类型0：都不选1：提交申请时，员工不可修改固定审批人2：提交申请时，员工不可删除固定抄送人")
    private List<Integer> modifyPermissionType;

    @ApiModelProperty(value = "是否开启限时配置:1是,0否")
    private Integer limitTimeStatus;

    @ApiModelProperty(value = "限时配置")
    private ExamineAdvancedConfigLimitTimeVO advancedLimitTimeVO;

    @ApiModelProperty(value = "是否可转交 0 否 1 是")
    private Boolean isTransferable;

    @ApiModelProperty(value = "征求他人意见权限  0 否 1 是")
    private Boolean isConsult;

    @ApiModelProperty("是否开启审批流程选择 0 否 1 是")
    private Boolean isRejectChooseFlow;

    @ApiModelProperty("发起人是否可撤回审批 0 否 1 是")
    private Boolean isCreateUserWithdraw;

    @ApiModelProperty("当前审批id")
    private Long examineFlowId;

    @ApiModelProperty("操作权限（十进制转二进制） 终止、作废、归档")
    private Integer privilege;
}
