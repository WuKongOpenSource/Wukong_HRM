package com.kakarote.hrm.entity.BO;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("消息列表查询对象")
public class AdminMessageQueryBO extends PageEntity {

    @ApiModelProperty("是否已读")
    private Integer isRead;

    @ApiModelProperty("label")
    private Integer label;

    @ApiModelProperty("忽略label")
    private List<Integer> igLabel;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("type")
    private Integer type;

}
