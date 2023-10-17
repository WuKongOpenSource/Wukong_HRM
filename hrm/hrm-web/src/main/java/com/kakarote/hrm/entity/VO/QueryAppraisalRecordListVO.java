package com.kakarote.hrm.entity.VO;

import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.hrm.entity.PO.HrmAppraisalActionRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryAppraisalRecordListVO extends HrmAppraisalActionRecord {
    /**
     * private AdminUser adminUser
     */
    @ApiModelProperty("真实姓名")
    private String realname;

    @ApiModelProperty("附件")
    private FileEntity fileEntity;

    @Override
    public String toString() {
        return "QueryRecordListVO{" +
                " realname='" + realname + '\'' +
                '}';
    }
}
