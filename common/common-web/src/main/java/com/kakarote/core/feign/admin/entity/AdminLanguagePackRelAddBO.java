package com.kakarote.core.feign.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 语言包表
 * </p>
 *
 * @author zmj
 * @since 2020-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AdminLanguagePackRelAddBO对象", description="语言包表")
public class AdminLanguagePackRelAddBO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "语言包关联")
    private List<AdminLanguagePackRelBO> relBOList;


}
