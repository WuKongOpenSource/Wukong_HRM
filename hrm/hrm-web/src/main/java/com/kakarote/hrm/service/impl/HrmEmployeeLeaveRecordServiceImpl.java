package com.kakarote.hrm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.entity.BO.QueryLeaveRecordPageListBO;
import com.kakarote.hrm.entity.BO.SetEmployeeLeaveRecordB0;
import com.kakarote.hrm.entity.PO.HrmAttendanceExamine;
import com.kakarote.hrm.entity.PO.HrmEmployeeLeaveRecord;
import com.kakarote.hrm.mapper.HrmEmployeeLeaveRecordMapper;
import com.kakarote.hrm.service.IHrmAttendanceExamineService;
import com.kakarote.hrm.service.IHrmEmployeeLeaveRecordService;
import com.kakarote.hrm.service.IHrmEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工请假记录 服务实现类
 * </p>
 *
 * @author guomenghao
 * @since 2021-08-13
 */
@Service
public class HrmEmployeeLeaveRecordServiceImpl extends BaseServiceImpl<HrmEmployeeLeaveRecordMapper, HrmEmployeeLeaveRecord> implements IHrmEmployeeLeaveRecordService {

    @Autowired
    private HrmEmployeeLeaveRecordMapper leaveRecordMapper;

    @Autowired
    private IHrmAttendanceExamineService attendanceExamineService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Override
    public BasePage<Map<String, Object>> queryLeaveRecordPageList(QueryLeaveRecordPageListBO leaveRecordPageListBO) {
        queryOaLeaveExamineList();
        String sortField = StrUtil.isNotEmpty(leaveRecordPageListBO.getSortField()) ? StrUtil.toUnderlineCase(leaveRecordPageListBO.getSortField()) : null;
        leaveRecordPageListBO.setSortField(sortField);
        BasePage<Map<String, Object>> page = leaveRecordMapper.queryLeaveRecordPageList(leaveRecordPageListBO.parse(), leaveRecordPageListBO);
        return page;
    }


    @Override
    public List<HrmEmployeeLeaveRecord> queryLeaveRecord(LocalDateTime leaveTime,Long employeeId) {
        return leaveRecordMapper.queryLeaveRecord(leaveTime,employeeId);
    }

    @Override
    public void addEmployeeLeaveRecord(SetEmployeeLeaveRecordB0 employeeLeaveRecord) {
        HrmEmployeeLeaveRecord hrmEmployeeLeaveRecord = BeanUtil.copyProperties(employeeLeaveRecord, HrmEmployeeLeaveRecord.class);
        save(hrmEmployeeLeaveRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void queryOaLeaveExamineList() {
        HrmAttendanceExamine hrmAttendanceExamine = attendanceExamineService.queryHrmAttendanceExamine();
        if (ObjectUtil.isNotNull(hrmAttendanceExamine)) {
        }
    }

    @Override
    public HrmEmployeeLeaveRecord queryStartOrEndLeaveRecord(LocalDateTime currentDate, Long employeeId) {
        return leaveRecordMapper.queryStartOrEndLeaveRecord(currentDate, employeeId);
    }

    @Override
    public Integer queryLeaveEmpCount(String currentDate, Collection<Long> employeeIds) {
        queryOaLeaveExamineList();
        return baseMapper.queryLeaveEmpCount(currentDate, employeeIds);
    }

    /**
     * 查询请假类型
     *
     * @return
     */
    @Override
    public List<String> queryLeaveTypeList() {
        List<String> leaveTypes = new ArrayList<>();
        return leaveTypes;
    }
}
