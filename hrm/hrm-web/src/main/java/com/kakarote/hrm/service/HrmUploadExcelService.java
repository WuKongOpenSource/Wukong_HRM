package com.kakarote.hrm.service;

import com.kakarote.hrm.entity.BO.UploadExcelBO;
import org.springframework.web.multipart.MultipartFile;

public interface HrmUploadExcelService {

    /**
     * 导入excel
     */
    public Long uploadExcel(MultipartFile file, UploadExcelBO uploadExcelBO);

    /**
     * 考勤打卡原始数据导入
     *
     * @param file
     * @param uploadExcelBO
     * @return
     */
    Long uploadAttendanceExcel(MultipartFile file, UploadExcelBO uploadExcelBO);
}
