package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_admin_dept")
@ApiModel(value="AdminDept对象", description="部门表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeptCO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "dept_id",type = IdType.ASSIGN_ID)
    private Long deptId;

    @ApiModelProperty(value = "父级ID 顶级部门为0")
    private Long parentId;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "排序 越大越靠后")
    private Integer num;

    @ApiModelProperty(value = "部门备注，企业微信的部门ID保存到这个里面")
    private String remark;

    @ApiModelProperty(value = "公司ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long companyId;

    @ApiModelProperty(value = "部门负责人")
    private Long ownerUserId;

    @ApiModelProperty("当前部门在职人数")
    @TableField(exist = false)
    private Integer currentNum;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}
