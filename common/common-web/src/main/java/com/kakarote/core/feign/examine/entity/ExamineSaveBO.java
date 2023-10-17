package com.kakarote.core.feign.examine.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("审批数据保存BO")
public class ExamineSaveBO {

    @ApiModelProperty(value = "审批ID")
    private Long examineId;

    @ApiModelProperty(value = "0 OA 1 合同 2 回款 3发票 4薪资 5 采购审核 6采购退货审核 7销售审核 8 销售退货审核 9付款单审核10 回款单审核11盘点审核12调拨审核 20 阶段流程")
    private Integer label;

    @ApiModelProperty(value = "审批名称")
    private String examineName;

    @ApiModelProperty("图标")
    private Map<String,Object> examineIcon;

    @ApiModelProperty(value = "审核拒绝后的处理方式 1 返回初始层级 2 从拒绝层级开始")
    private Integer recheckType;

    @ApiModelProperty(value = "用户列表")
    private List<Long> userList;

    @ApiModelProperty(value = "部门列表")
    private List<Long> deptList;

    @ApiModelProperty(value = "是否展示子级部门 0不需要 1 需要")
    private Integer isNeedChild;

    @ApiModelProperty(value = "审批管理员列表")
    private List<Long> managerList;

    @ApiModelProperty(value = "1 正常 2 停用 3 删除 ")
    private Integer status = 1;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "1 普通审批 2 请假审批 3 出差审批 4 加班审批 5 差旅报销 6 借款申请 0 自定义审批")
    private Integer oaType;

    @ApiModelProperty(value = "所属分组")
    private Long groupId;

    @ApiModelProperty(value = "审批节点数据")
    private List<ExamineDataSaveBO> dataList;

    @ApiModelProperty(value = "审批高级配置")
    private ExamineAdvancedConfigBO advancedConfigBO;

    public Integer getIsNeedChild() {
        if (isNeedChild == null) {
            isNeedChild =0;
        }
        return isNeedChild;
    }

}
