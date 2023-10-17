package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.exception.CrmException;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.hrm.common.HrmCodeEnum;
import com.kakarote.hrm.constant.MenuIdConstant;
import com.kakarote.hrm.entity.BO.QueryCoefficientBO;
import com.kakarote.hrm.entity.BO.QueryEmployeeAchievementFileBO;
import com.kakarote.hrm.entity.DTO.EmployeeAchievementFileReq;
import com.kakarote.hrm.entity.DTO.OperationReq;
import com.kakarote.hrm.entity.PO.HrmAppraisalEmployee;
import com.kakarote.hrm.entity.PO.HrmAppraisalPlanResultSetting;
import com.kakarote.hrm.entity.PO.HrmEmployeeAchievementFile;
import com.kakarote.hrm.entity.VO.EmployeeAchievementFileVO;
import com.kakarote.hrm.mapper.HrmEmployeeAchievementFileMapper;
import com.kakarote.hrm.service.IHrmAppraisalEmployeeService;
import com.kakarote.hrm.service.IHrmAppraisalPlanResultSettingService;
import com.kakarote.hrm.service.IHrmEmployeeAchievementFileService;
import com.kakarote.hrm.utils.EmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工绩效档案 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2022-06-14
 */
@Service
public class HrmEmployeeAchievementFileServiceImpl extends BaseServiceImpl<HrmEmployeeAchievementFileMapper, HrmEmployeeAchievementFile> implements IHrmEmployeeAchievementFileService {

    @Autowired
    private EmployeeUtil employeeUtil;

    @Autowired
    private IHrmAppraisalPlanResultSettingService appraisalPlanResultSettingService;

    @Autowired
    private IHrmAppraisalEmployeeService appraisalEmployeeService;

    @Override
    public BasePage<EmployeeAchievementFileVO> queryEmployeeAchievementFileList(QueryEmployeeAchievementFileBO employeeAchievementFileBO) {
        Collection<Long> employeeIds = employeeUtil.queryDataAuthEmpIdByMenuId(MenuIdConstant.ACHIEVEMENT_FILE_MENU_ID);
        return baseMapper.queryEmployeeAchievementFileList(employeeAchievementFileBO.parse(), employeeAchievementFileBO, employeeIds);
    }

    @Override
    public void addOrUpdate(Long appraisalEmployeeId, Long employeeId) {
        HrmEmployeeAchievementFile employeeAchievementFile = lambdaQuery().eq(HrmEmployeeAchievementFile::getEmployeeId, employeeId).one();
        if (ObjectUtil.isNotNull(employeeAchievementFile)) {
            lambdaUpdate().set(HrmEmployeeAchievementFile::getUpdateTime, new Date()).set(HrmEmployeeAchievementFile::getAppraisalCount, employeeAchievementFile.getAppraisalCount() + 1).set(HrmEmployeeAchievementFile::getRecentlyAppraisalEmployeeId, appraisalEmployeeId).eq(HrmEmployeeAchievementFile::getEmployeeId, employeeId).update();
        } else {
            HrmEmployeeAchievementFile achievementFile = new HrmEmployeeAchievementFile();
            achievementFile.setCreateTime(new Date());
            achievementFile.setEmployeeId(employeeId);
            achievementFile.setAppraisalCount(1);
            achievementFile.setRecentlyAppraisalEmployeeId(appraisalEmployeeId);
            save(achievementFile);
        }
    }

    @Override
    public Map<Long, Double> queryCoefficient(QueryCoefficientBO queryCoefficientBO) {
        Map<Long, Double> employeeCoefficient = new HashMap<>();

        StringBuffer paidForMonth = new StringBuffer();
        paidForMonth.append(queryCoefficientBO.getYear());
        paidForMonth.append("-");
        if (queryCoefficientBO.getMonth() < 10) {
            paidForMonth.append("0");
        }
        paidForMonth.append(queryCoefficientBO.getMonth());
        if (CollectionUtil.isNotEmpty(queryCoefficientBO.getEmployeeIdList())) {
            queryCoefficientBO.getEmployeeIdList().stream().forEach(
                    employeeId -> {
                        HrmAppraisalEmployee appraisalEmployee = baseMapper.queryRecentlyAppraisal(employeeId, paidForMonth.toString());
                        if (ObjectUtil.isNotNull(appraisalEmployee)) {
                            HrmAppraisalPlanResultSetting resultSetting = appraisalPlanResultSettingService.lambdaQuery().eq(HrmAppraisalPlanResultSetting::getAppraisalPlanId, appraisalEmployee.getAppraisalPlanId()).eq(HrmAppraisalPlanResultSetting::getLevelName, appraisalEmployee.getLevel()).one();
                            employeeCoefficient.put(employeeId, resultSetting.getCoefficient());
                        }
                    }
            );
        }
        return employeeCoefficient;
    }

    @Transactional
    @Override
    public void delAppraisalFileRecordList(EmployeeAchievementFileReq operationReq) {
        Optional<HrmEmployeeAchievementFile> achievementFile = lambdaQuery().eq(HrmEmployeeAchievementFile::getEmployeeId, operationReq.getEmployeeId()).oneOpt();
        if (achievementFile.isPresent()) {
            Integer delCount = appraisalEmployeeService.deleteAppraisalEmployeeById(operationReq.getIds());
            Integer curCount = achievementFile.get().getAppraisalCount();
            Integer count = curCount - delCount;
            Optional<HrmAppraisalEmployee> recentlyAppraisalEmployee = appraisalEmployeeService.lambdaQuery().eq(HrmAppraisalEmployee::getEmployeeId, operationReq.getEmployeeId()).orderByDesc(HrmAppraisalEmployee::getCreateTime).last("limit 1").oneOpt();
            if (recentlyAppraisalEmployee.isPresent()) {
                lambdaUpdate().set(HrmEmployeeAchievementFile::getRecentlyAppraisalEmployeeId, recentlyAppraisalEmployee.get().getAppraisalEmployeeId()).set(HrmEmployeeAchievementFile::getAppraisalCount, count).set(HrmEmployeeAchievementFile::getUpdateTime, new Date()).eq(HrmEmployeeAchievementFile::getEmployeeId, operationReq.getEmployeeId()).update();
            } else {
                lambdaUpdate().eq(HrmEmployeeAchievementFile::getEmployeeId, operationReq.getEmployeeId()).remove();
            }
        }
    }

    @Transactional
    @Override
    public void delAppraisalFileRecordListOfAll(OperationReq operationReq) {
        List<HrmEmployeeAchievementFile> employeeAchievementFileList = lambdaQuery().in(HrmEmployeeAchievementFile::getAchievementFileId, operationReq.getIds()).list();
        if (CollectionUtil.isEmpty(employeeAchievementFileList)) {
            throw new CrmException(HrmCodeEnum.RESULT_NULL_ERROR);
        }
        lambdaUpdate().in(HrmEmployeeAchievementFile::getAchievementFileId, operationReq.getIds()).remove();
        List<Long> employeeIdList = employeeAchievementFileList.stream().map(HrmEmployeeAchievementFile::getEmployeeId).collect(Collectors.toList());
        appraisalEmployeeService.deleteAppraisalEmployeeByEmployeeId(employeeIdList);
    }

    @Transactional
    @Override
    public void delAppraisalRecordByAppraisalPlanId(Long appraisalPlanId) {
        List<HrmAppraisalEmployee> appraisalEmployeeList = appraisalEmployeeService.queryEmployeeListByPlanId(appraisalPlanId);
        if (CollectionUtil.isNotEmpty(appraisalEmployeeList)) {
            for (HrmAppraisalEmployee appraisalEmployee : appraisalEmployeeList) {
                HrmEmployeeAchievementFile employeeAchievementFile = baseMapper.queryAchievementFileByEmployeeId(appraisalEmployee.getEmployeeId());
                if (ObjectUtil.isNotEmpty(employeeAchievementFile)) {
                    Integer count = 0;
                    if (employeeAchievementFile.getAppraisalCount() >= 1) {
                        count = employeeAchievementFile.getAppraisalCount() - 1;
                    }
                    HrmAppraisalEmployee recentlyAppraisalEmployee = appraisalEmployeeService.queryRecentlyAppraisalEmployee(appraisalEmployee.getEmployeeId());
                    if (ObjectUtil.isNotEmpty(recentlyAppraisalEmployee)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("appraisalEmployeeId", recentlyAppraisalEmployee.getAppraisalEmployeeId());
                        param.put("appraisalCount", count);
                        param.put("updateTime", new Date());
                        param.put("employeeId", appraisalEmployee.getEmployeeId());
                        baseMapper.updateAchievementCount(param);
//                        lambdaUpdate().set(HrmEmployeeAchievementFile::getRecentlyAppraisalEmployeeId, recentlyAppraisalEmployee.getAppraisalEmployeeId()).set(HrmEmployeeAchievementFile::getAppraisalCount, count).set(HrmEmployeeAchievementFile::getUpdateTime, new Date()).eq(HrmEmployeeAchievementFile::getEmployeeId, appraisalEmployee.getEmployeeId()).update();
                    } else {
                        baseMapper.removeByEmployeeId(appraisalEmployee.getEmployeeId());
//                        lambdaUpdate().eq(HrmEmployeeAchievementFile::getEmployeeId, appraisalEmployee.getEmployeeId()).remove();
                    }
                }
            }
        }
    }

}
