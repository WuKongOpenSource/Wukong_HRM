package com.kakarote.core.feign.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目成员表
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ProjectUser对象", description="项目成员表")
public class ProjectUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "成员ID")
    private Long userId;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;


}
