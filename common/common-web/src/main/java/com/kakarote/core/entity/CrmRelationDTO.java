package com.kakarote.core.entity;

import com.kakarote.core.feign.crm.entity.SimpleCrmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @program: wk_crm
 * @className OaRelationDTO
 * @description:
 * @author: jiao sir
 * @create: 2021-08-04 17:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "crm关系DTO对象")
public class CrmRelationDTO {

    @ApiModelProperty(value = "客户list")
    private List<SimpleCrmEntity> customerList;
    @ApiModelProperty(value = "商机list")
    private List<SimpleCrmEntity> businessList;
    @ApiModelProperty(value = "联系人list")
    private List<SimpleCrmEntity> contactsList;
    @ApiModelProperty(value = "合同list")
    private List<SimpleCrmEntity> contractList;
    @ApiModelProperty(value = "回款列表")
    private List<SimpleCrmEntity> receivablesList;
    @ApiModelProperty(value = "产品list")
    private List<SimpleCrmEntity> productList;
    @ApiModelProperty(value = "moduleList")
    private List<SimpleCrmEntity> moduleList;
}
