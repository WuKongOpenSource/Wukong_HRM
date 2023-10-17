package com.kakarote.hrm.mapper;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.PO.HrmSalaryGroup;
import com.kakarote.hrm.entity.VO.SalaryGroupPageListVO;

import java.util.List;

/**
 * <p>
 * 薪资组 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-26
 */
public interface HrmSalaryGroupMapper extends BaseMapper<HrmSalaryGroup> {

    BasePage<SalaryGroupPageListVO> querySalaryGroupPageList(BasePage<SalaryGroupPageListVO> parse);

    List<HrmSalaryGroup> querySalaryGroupByTaxType(int taxType);
}
