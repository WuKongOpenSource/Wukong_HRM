package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author JiaS
 * @date 2020/12/23
 */
@Data
@ApiModel("审批预览VO")
public class ExaminePreviewVO {


    @ApiModelProperty(value = "撤回之后重新审核操作 1 从第一层开始 2 从拒绝的层级开始")
    private Integer recheckType;

    @ApiModelProperty(value = "审批管理员列表")
    private List<Long> managerList;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "修改权限类型0：都不选1：提交申请时，员工不可修改固定审批人2：提交申请时，员工不可删除固定抄送人")
    private List<Integer> modifyPermissionType;

    @ApiModelProperty("流程信息")
    private List<ExamineFlowVO>  examineFlowList;

    @ApiModelProperty("高级设置")
    private ExamineAdvancedConfigVO examineAdvancedConfigVO;

    @ApiModelProperty(value = "语言包map")
    private Map<String,String> languageKeyMap;

}
