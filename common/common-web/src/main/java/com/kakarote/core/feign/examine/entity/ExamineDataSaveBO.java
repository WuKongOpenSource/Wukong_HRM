package com.kakarote.core.feign.examine.entity;

import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("审批流程保存BO")
public class ExamineDataSaveBO {

    @ApiModelProperty("流程id")
    private Long uniqueId;

    @ApiModelProperty("0 条件 1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管 7抄送节点 8成员组件审批")
    private Integer examineType;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("找不到人员时的处理方案 1 自动通过 2 管理员审批")
    private Integer examineErrorHandling;

    @ApiModelProperty("多人审批类型 1 依次审批 2 会签 3 或签  \n" +
            "当审批类型为主管时 找不到上级时，是否由上一级上级代审批 0 否 1 是 \n" +
            "当审批类型为连续多级主管时 1 指定角色 2 组织架构的最上级 ")
    private Integer type;

    @ApiModelProperty("选择范围，只有发起人自选需要 1 全公司 2 指定成员 3 指定角色 ")
    private Integer rangeType;

    @ApiModelProperty("选择类型，只有发起人自选需要 1 自选一人 2 自选多人")
    private Integer chooseType;

    @ApiModelProperty("指定审批人")
    private List<SimpleUser> memberUserList;

    @ApiModelProperty("用户列表")
    private List<Long> userList;

    @ApiModelProperty("部门列表")
    private List<Long> deptList;

    @ApiModelProperty("指定上级列表")
    private List<Integer> parentLevelList;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("指定角色")
    private List<Long> roleIdList;

    @ApiModelProperty("抄送给发起人本人 1是0否，")
    private Integer isSelf;

    @ApiModelProperty("是否允许发起人添加抄送人1是0否，")
    private Integer isAdd;

    @ApiModelProperty("直属上级级别 1 代表直属上级 2 代表 直属上级的上级\n" +
            "连续多级审批的最高级别")
    private Integer parentLevel;

    @ApiModelProperty("条件审批列表")
    private List<ExamineConditionSaveBO> conditionList;

    @ApiModelProperty(value = "指定审批人字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "指定审批人字段名称")
    private String fieldName;


    @ApiModelProperty(value = "字段授权列表")
    private List<ExamineFieldAuthBO> fieldAuthList;
}
