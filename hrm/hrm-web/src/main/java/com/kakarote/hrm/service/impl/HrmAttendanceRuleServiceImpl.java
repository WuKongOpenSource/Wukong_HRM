package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.IsEnum;
import com.kakarote.hrm.entity.BO.SetAttendanceRuleBO;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroup;
import com.kakarote.hrm.entity.PO.HrmAttendanceRule;
import com.kakarote.hrm.entity.VO.AttendanceRulePageListVO;
import com.kakarote.hrm.mapper.HrmAttendanceRuleMapper;
import com.kakarote.hrm.service.IHrmAttendanceGroupService;
import com.kakarote.hrm.service.IHrmAttendanceRuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 打卡规则表 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-10
 */
@Service
public class HrmAttendanceRuleServiceImpl extends BaseServiceImpl<HrmAttendanceRuleMapper, HrmAttendanceRule> implements IHrmAttendanceRuleService {

    @Resource
    private HrmAttendanceRuleMapper attendanceRuleMapper;
    @Resource
    private IHrmAttendanceGroupService attendanceGroupService;

    @Override
    public BasePage<AttendanceRulePageListVO> queryAttendanceRulePageList(PageEntity pageEntity) {
        BasePage<HrmAttendanceRule> attendanceRuleBasePage = attendanceRuleMapper.selectPage(pageEntity.parse(), Wrappers.emptyWrapper());
        List<AttendanceRulePageListVO> attendanceRulePageListVOList = new ArrayList<>();
        attendanceRuleBasePage.getList().forEach(attendanceRule -> {
            AttendanceRulePageListVO attendanceRulePageListVO = new AttendanceRulePageListVO();
            BeanUtil.copyProperties(attendanceRule, attendanceRulePageListVO);
            attendanceRulePageListVOList.add(attendanceRulePageListVO);
        });
        BasePage<AttendanceRulePageListVO> page = new BasePage<>(attendanceRuleBasePage.getCurrent(), attendanceRuleBasePage.getSize(), attendanceRuleBasePage.getTotal());
        page.setList(attendanceRulePageListVOList);
        return page;
    }

    @Override
    public void setAttendanceRule(SetAttendanceRuleBO attendanceRule) {
        HrmAttendanceRule hrmAttendanceRule = BeanUtil.copyProperties(attendanceRule, HrmAttendanceRule.class);
        saveOrUpdate(hrmAttendanceRule);
    }

    @Override
    public void verifyAttendanceRuleName(SetAttendanceRuleBO attendanceRule) {
        List<HrmAttendanceRule> attendanceRuleList = lambdaQuery().eq(HrmAttendanceRule::getAttendanceRuleName, attendanceRule.getAttendanceRuleName())
                .ne(attendanceRule.getAttendanceRuleId() != null, HrmAttendanceRule::getAttendanceRuleId, attendanceRule.getAttendanceRuleId()).list();
        if (attendanceRuleList.size() > 0) {
            throw new CrmException(HrmCodeEnum.RULE_NAME_ALREADY_EXISTS, attendanceRuleList.get(0).getAttendanceRuleName());
        }
    }

    @Override
    public OperationLog deleteAttendanceRule(Long attendanceRuleId) {
        HrmAttendanceRule hrmAttendanceRule = attendanceRuleMapper.selectById(attendanceRuleId);
        if (hrmAttendanceRule.getIsDefaultSetting() == IsEnum.YES.getValue()) {
            throw new CrmException(HrmCodeEnum.ATTEND_DEFAULT_CANNOT_BE_DELETED, "考勤规则");
        }
        List<HrmAttendanceGroup> hrmAttendanceGroupList = attendanceGroupService.lambdaQuery().eq(HrmAttendanceGroup::getAttendanceRuleId, attendanceRuleId).list();
        if (CollUtil.isNotEmpty(hrmAttendanceGroupList)) {
            throw new CrmException(HrmCodeEnum.ATTEND_USED_CANNOT_BE_DELETED, "考勤规则");
        }
        attendanceRuleMapper.deleteById(attendanceRuleId);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(hrmAttendanceRule.getAttendanceRuleName());
        operationLog.setOperationInfo("删除考勤扣款：" + hrmAttendanceRule.getAttendanceRuleName());
        return operationLog;
    }
}
