package com.kakarote.hrm.service.excel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.kakarote.core.common.cache.AdminCacheKey;
import com.kakarote.core.exception.CrmException;
import com.kakarote.hrm.entity.BO.SetFixSalaryRecordBO;
import com.kakarote.hrm.entity.BO.UploadExcelBO;
import com.kakarote.hrm.entity.PO.HrmEmployee;
import com.kakarote.hrm.entity.VO.ChangeSalaryOptionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FixSalaryExport extends ExcelImport {

    private static final int SEVEN = 7;

    public FixSalaryExport(UploadExcelBO uploadExcelBO, MultipartFile file) {
        uploadExcelBO.setFilePath(getFilePath(file));
        super.uploadExcelBO = uploadExcelBO;
    }

    @Override
    public void importExcel() {
        List<ChangeSalaryOptionVO> salaryOptions = archivesService.queryChangeSalaryExcelExportOption().stream().map(option -> {
            ChangeSalaryOptionVO changeSalaryOptionVO = new ChangeSalaryOptionVO();
            changeSalaryOptionVO.setName(option.getName());
            changeSalaryOptionVO.setCode(option.getCode());
            return changeSalaryOptionVO;
        }).collect(Collectors.toList());
        List<ChangeSalaryOptionVO> proSalaryOptions = salaryOptions.stream().filter(option -> option.getCode() >= 10100 && option.getCode() <= 10103).map(option -> {
            ChangeSalaryOptionVO changeSalaryOptionVO = new ChangeSalaryOptionVO();
            changeSalaryOptionVO.setName(option.getName());
            changeSalaryOptionVO.setCode(option.getCode());
            return changeSalaryOptionVO;
        }).collect(Collectors.toList());
        ExcelUtil.readBySax(uploadExcelBO.getFilePath(), 0, (int sheetIndex, long rowIndex, List<Object> rowList) -> {
            try {
                int oneTimeMaxMessage = 10001;
                if (this.num.incrementAndGet() > oneTimeMaxMessage) {
                    rowList.add(0, "最多同时导入10000条数据");
                    errorList.add(rowList);
                    return;
                }
                redis.setex(AdminCacheKey.UPLOAD_EXCEL_MESSAGE_PREFIX + getUploadExcelBO().getMessageId().toString(), UPLOAD_EXCEL_EXIST_TIME, Math.max(num.get() - 2, 0));
                int six = 6;
                if (rowIndex == six) {
                    rowList.add(0, "错误原因");
                    errorList.add(rowList);
                }
                if (rowIndex < SEVEN) {
                    return;
                }
                SetFixSalaryRecordBO setFixSalaryRecordBO = new SetFixSalaryRecordBO();
                String jobNumber = Optional.ofNullable(rowList.get(1)).map(String::valueOf).orElse("");
                if (StrUtil.isEmpty(jobNumber)) {
                    rowList.add(0, "请填写工号");
                    errorList.add(rowList);
                    return;
                } else {
                    Optional<HrmEmployee> employeeOpt = employeeService.lambdaQuery().select(HrmEmployee::getEmployeeId).eq(HrmEmployee::getIsDel, 0).eq(HrmEmployee::getJobNumber, jobNumber).oneOpt();
                    if (employeeOpt.isPresent()) {
                        setFixSalaryRecordBO.setEmployeeId(employeeOpt.get().getEmployeeId());
                    } else {
                        rowList.add(0, "工号对应的员工不存在");
                        errorList.add(rowList);
                        return;
                    }
                }
                BigDecimal proSum = new BigDecimal(0);
                BigDecimal sum = new BigDecimal(0);
                for (int i = 0; i < proSalaryOptions.size(); i++) {
                    ChangeSalaryOptionVO changeSalaryOption = proSalaryOptions.get(i);
                    int index = i + 4;
                    if (rowList.size() > index) {
                        String value = getValue(rowList.get(index));
                        String[] split = value.split(",");
                        if (split[0].length() > 7 || (split.length == 2 && split[1].length() > 2)) {
                            rowList.add(0, "工资数据格式不正确");
                            errorList.add(rowList);
                            return;
                        }
                        changeSalaryOption.setValue(value);
                        proSum = proSum.add(new BigDecimal(value));
                    } else {
                        changeSalaryOption.setValue("0");
                        proSum = proSum.add(new BigDecimal(0));
                    }
                }
                for (int i = 0; i < salaryOptions.size(); i++) {
                    ChangeSalaryOptionVO changeSalaryOption = salaryOptions.get(i);
                    int index = i + 7;
                    if (rowList.size() > index) {
                        String value = getValue(rowList.get(index));
                        String[] split = value.split(",");
                        if (split[0].length() > 7 || (split.length == 2 && split[1].length() > 2)) {
                            rowList.add(0, "工资数据格式不正确");
                            errorList.add(rowList);
                            return;
                        }
                        changeSalaryOption.setValue(value);
                        sum = sum.add(new BigDecimal(value));
                    } else {
                        changeSalaryOption.setValue("0");
                        sum = sum.add(new BigDecimal(0));
                    }
                }
                setFixSalaryRecordBO.setProSalary(proSalaryOptions);
                setFixSalaryRecordBO.setProSum(proSum.toString());
                setFixSalaryRecordBO.setSalary(salaryOptions);
                setFixSalaryRecordBO.setSum(sum.toString());
                if (rowList.size() > (salaryOptions.size() + SEVEN)) {
                    setFixSalaryRecordBO.setRemarks(Optional.ofNullable(rowList.get(salaryOptions.size() + 7)).map(String::valueOf).orElse("0"));
                }
                archivesService.setFixSalaryRecord(setFixSalaryRecordBO);
                updateNum.incrementAndGet();
            } catch (CrmException e) {
                log.error("导入异常", e);
                rowList.add(0, e.getMsg());
                errorList.add(rowList);
            } catch (Exception e) {
                log.error("导入异常", e);
                rowList.add(0, "未知异常");
                errorList.add(rowList);
            }
        });
    }

}
