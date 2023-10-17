package com.kakarote.hrm.entity.BO;

import com.kakarote.hrm.entity.VO.HrmModelFiledVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * crm模块保存的BO
 *
 * @author zhangzhiwei
 */
@Data
@ToString
@ApiModel("crm保存对象")
public class HrmModelSaveBO {

    @ApiModelProperty(value = "实体类对象")
    private Map<String, Object> entity;

    @ApiModelProperty(value = "自定义字段对象")
    private List<HrmModelFiledVO> field;
}
