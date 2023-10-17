package com.kakarote.hrm.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.EmployeeHolder;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.entity.BO.QueryNotesStatusBO;
import com.kakarote.hrm.entity.PO.HrmNotes;
import com.kakarote.hrm.mapper.HrmNotesMapper;
import com.kakarote.hrm.service.IHrmNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 备忘 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-07-13
 */
@Service
public class HrmNotesServiceImpl extends BaseServiceImpl<HrmNotesMapper, HrmNotes> implements IHrmNotesService {

    @Autowired
    private HrmNotesMapper notesMapper;

    @Override
    public void addNotes(HrmNotes notes) {
        notes.setEmployeeId(EmployeeHolder.getEmployeeId());
        if (ObjectUtil.isNull(notes.getEmployeeId())) {
            throw new CrmException(HrmCodeEnum.EMPLOYEE_ACCOUNT_NO_EXIST);
        }
        save(notes);
    }

    @Override
    public List<HrmNotes> queryNoteListByTime(LocalDate time, Collection<Long> employeeIds) {
        return notesMapper.queryNoteListByTime(time, employeeIds);
    }

    @Override
    public Set<String> queryNoteStatusList(QueryNotesStatusBO queryNotesStatusBO, Collection<Long> employeeIds) {
        return notesMapper.queryNoteStatusList(queryNotesStatusBO, employeeIds);
    }
}
