package com.kakarote.core.feign.oa.entity;

import com.kakarote.core.feign.admin.entity.FileEntity;
import com.kakarote.core.feign.admin.entity.SimpleUser;
import com.kakarote.core.feign.crm.entity.SimpleCrmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(value="ExamineVO对象", description="审批表VO对象")
public class ExamineVO extends OaExamine {

    @ApiModelProperty("审批步骤id")
    private Long examineStepId;
    @ApiModelProperty("审批类型标题")
    private String categoryTitle;
    @ApiModelProperty("审批类型")
    private Integer type;
    @ApiModelProperty("图标")
    private String icon;
    @ApiModelProperty("审批类型")
    private SimpleUser createUser;

    @ApiModelProperty("上次处理人")
    private SimpleUser lastHandleUser;

    //--------------------------------
    //--------------------------------
    @ApiModelProperty("oa状态 examine_flag 3归档 4作废")
    private Integer examineFlag;
    @ApiModelProperty("当前审批人")
    private String currentExamineUser;
    @ApiModelProperty("当前审批停留时间")
    private String currentStopTime;
    //--------------------------------

    @ApiModelProperty("流程名称")
    private String examineName;

    //----------------------------------------------
    //添加是否关注
    @ApiModelProperty("是否关注  0未关注  1已关注")
    private Integer followStatus=0;
    //----------------------------------------------

    @ApiModelProperty("图片")
    private List<FileEntity> img;

    @ApiModelProperty("附件")
    private List<FileEntity> file;
    @ApiModelProperty("")
    private String causeTitle;

    @ApiModelProperty("权限")
    private Map<String,Integer> permission;

    @ApiModelProperty("客户")
    private List<SimpleCrmEntity> customerList;
    @ApiModelProperty("联系人")
    private List<SimpleCrmEntity> contactsList;
    @ApiModelProperty("商机")
    private List<SimpleCrmEntity> businessList;
    @ApiModelProperty("合同")
    private List<SimpleCrmEntity> contractList;
    @ApiModelProperty("OA审批记录")
    private OaExamineRecord record;

    @ApiModelProperty("差旅审批记录")
    private List<OaExamineTravel> examineTravelList;

    @Override
    public String toString() {
        return "ExamineVO{" +
                "examineStepId=" + examineStepId +
                ", categoryTitle='" + categoryTitle + '\'' +
                ", type=" + type +
                ", icon='" + icon + '\'' +
                ", createUser=" + createUser +
                ", examineName='" + examineName + '\'' +
                ", img=" + img +
                ", file=" + file +
                ", causeTitle='" + causeTitle + '\'' +
                ", permission=" + permission +
                ", customerList=" + customerList +
                ", contactsList=" + contactsList +
                ", businessList=" + businessList +
                ", contractList=" + contractList +
                ", record=" + record +
                ", examineTravelList=" + examineTravelList +
                '}';
    }
}
