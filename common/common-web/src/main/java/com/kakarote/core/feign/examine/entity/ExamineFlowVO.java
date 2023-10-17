package com.kakarote.core.feign.examine.entity;

import com.kakarote.core.feign.admin.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("审批流程预览VO")
public class ExamineFlowVO {

    @ApiModelProperty("审核流程ID")
    private Long flowId;

    @ApiModelProperty("0 条件 1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管 6抄送审批节点")
    private Integer examineType;

    @ApiModelProperty("选择范围，只有发起人自选需要 1 全公司 2 指定成员 3 指定角色 ")
    private Integer rangeType;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "审批找不到用户或者条件均不满足时怎么处理 1 自动通过 2 管理员审批")
    private Integer examineErrorHandling;

    @ApiModelProperty(value = "直属上级级别 1 代表直属上级 2 代表 直属上级的上级")
    private Integer parentLevel;

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty("多人审批类型 1 依次审批 2 会签 3 或签")
    private Integer type;

    @ApiModelProperty("选择类型，只有发起人自选需要 1 自选一人 2 自选多人")
    private Integer chooseType;

    @ApiModelProperty(value = "节点标识（1：初始化，2：申请自定义添加，3：待办征求他人意见）")
    private Integer flowFlag;

    @ApiModelProperty(value = "插入目标节点")
    private Long insertTarget;

    @ApiModelProperty(value = "插入标识（1：之前，2：之后）")
    private Integer insertFlag;

    //------------------------------------
    @ApiModelProperty(value = "征求意见说明--0922添加")
    private String insertRemark;
    //------------------------------------

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty("用户列表")
    private List<SimpleUser> userList;

    @ApiModelProperty("最终用户列表")
    private List<SimpleUser> finalUserList;

    @ApiModelProperty("条件审批列表")
    private List<ExamineFlowConditionVO> conditionList;

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;

    @ApiModelProperty("指定用户列表")
    private List<Long> optionalUserList;

    @ApiModelProperty("指定上级列表")
    private List<Integer> parentLevelList;

    @ApiModelProperty("指定角色")
    private List<Long> roleIdList;

    @ApiModelProperty("指定角色信息")
    private List<Map<String, Object>> roleList;

    @ApiModelProperty("抄送给发起人本人 1是0否，")
    private Integer isSelf;

    @ApiModelProperty("是否允许发起人添加抄送人1是0否，")
    private Integer isAdd;

    @ApiModelProperty(value = "指定审批人字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "指定审批人字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段授权列表")
    private List<ExamineFieldAuthBO> fieldAuthList;

    @ApiModelProperty("")
    private Boolean isAdminSkip;

}
