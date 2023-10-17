package com.kakarote.hrm.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRecruitChannelBO {

    @ApiModelProperty("删除的渠道id")
    private Long deleteChannelId;

    @ApiModelProperty("员工变更的渠道id")
    private Long changeChannelId;

    @Override
    public String toString() {
        return "DeleteRecruitChannelBO{" +
                "deleteChannelId=" + deleteChannelId +
                ", changeChannelId=" + changeChannelId +
                '}';
    }
}
