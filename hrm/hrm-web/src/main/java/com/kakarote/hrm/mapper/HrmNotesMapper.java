package com.kakarote.hrm.mapper;

import com.kakarote.core.servlet.BaseMapper;
import com.kakarote.hrm.entity.BO.QueryNotesStatusBO;
import com.kakarote.hrm.entity.PO.HrmNotes;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 备忘 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-07-13
 */
public interface HrmNotesMapper extends BaseMapper<HrmNotes> {

    List<HrmNotes> queryNoteListByTime(@Param("time") LocalDate time, @Param("employeeIds") Collection<Long> employeeIds);

    Set<String> queryNoteStatusList(@Param("data") QueryNotesStatusBO queryNotesStatusBO, @Param("employeeIds") Collection<Long> employeeIds);
}
