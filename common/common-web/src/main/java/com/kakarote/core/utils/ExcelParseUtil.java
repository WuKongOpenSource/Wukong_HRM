package com.kakarote.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapProxy;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.core.common.MultipartFileUtil;
import com.kakarote.core.common.cache.AdminCacheKey;
import com.kakarote.core.common.enums.FieldEnum;
import com.kakarote.core.entity.UploadEntity;
import com.kakarote.core.feign.admin.service.AdminFileService;
import com.kakarote.core.feign.oa.entity.OaExamineTravel;
import com.kakarote.core.servlet.ApplicationContextHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelParseUtil {

    private static final ThreadLocal<Closeable> THREAD_LOCAL = new ThreadLocal<>();


    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list) {
        exportExcel(dataList, excelParseService, list, BaseUtil.getResponse(), 1);
    }

    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, HttpServletResponse response, Integer isXls) {
        exportExcel(dataList, excelParseService, list, response, isXls, true);
    }

    /**
     * 统一导出数据模板
     *
     * @param isClose 是否关闭writer流 true为关闭
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, HttpServletResponse response, Integer isXls, boolean isClose) {
        String key = StrPool.DASHED + UserUtil.getUserId();
        File file = null;
        String fileName;
        try {
            if (isXls == 1) {
                fileName = URLEncoder.encode(excelParseService.getExcelName() + "信息", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : "");
                file = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + key + fileName);
                exportExcelData(dataList, excelParseService, isXls, list, file);
            } else {
                fileName = URLEncoder.encode(excelParseService.getExcelName() + "信息" + ".csv", "UTF-8");
                file = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + key + fileName);
                exportExcelCsv(dataList, excelParseService, list, file);
            }
            if (!isClose) {
                List<? extends Map<String, Object>> nextData = excelParseService.getNextData();
                if (nextData != null && !nextData.isEmpty()) {
                    exportExcel(nextData, excelParseService, list, response, isXls, false);
                } else {
                    isClose = true;
                }
            }
            if (isClose) {
                if (isXls == 1) {
                    //自定义标题别名
                    //response为HttpServletResponse对象
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                    response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                    Closeable closeable = THREAD_LOCAL.get();
                    if (ObjectUtil.isNotEmpty(closeable)) {
                        ((ExcelWriter) closeable).flush();
                    }
                    FileUtil.writeToStream(file, response.getOutputStream());
                } else {
                    response.setContentType("application/csv;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                    //解决csv文件乱码问题
                    response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                    FileUtil.writeToStream(file, response.getOutputStream());
                }

                MultipartFile multipartFile = MultipartFileUtil.getMultipartFile(file);
                UploadEntity uploadEntity = ApplicationContextHolder.getBean(AdminFileService.class).uploadTempFile(multipartFile, null).getData();
                BaseUtil.getRedis().setex(AdminCacheKey.UPLOAD_EXCEL_MESSAGE_PREFIX + "file:" + uploadEntity.getFileId(), 604800, uploadEntity.getPath());
                response.setHeader("fileData", uploadEntity.getFileId());
                response.setHeader("exportSize", String.valueOf(dataList.size()));
            }
        } catch (Exception ex) {
            isClose = true;
            log.error("导出数据错误", ex);
        } finally {
            if (isClose) {
                IoUtil.close(THREAD_LOCAL.get());
                THREAD_LOCAL.remove();
                FileUtil.del(file);
            }
        }

    }


    /**
     * 统一导出数据模板
     */
    public static void exportExcelData(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, int isXls, List<?> list, File file) {
        try {
            ExcelWriter writer;
            boolean isInit = false;
            if (THREAD_LOCAL.get() == null) {
                writer = ObjectUtil.isNotEmpty(isXls) && 1 == isXls ? ExcelUtil.getBigWriter(10000) : ExcelUtil.getWriter();

                if (writer instanceof BigExcelWriter) {
                    writer.setDestFile(file);
                }
                THREAD_LOCAL.set(writer);
                isInit = true;
            } else {
                writer = (ExcelWriter) THREAD_LOCAL.get();
            }
            List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
            LinkedHashMap<String, Integer> headMap = new LinkedHashMap<>(headList.size(), 1.0f);

            List<String> headNameList = new LinkedList<>();

            for (ExcelDataEntity head : headList) {
                if (isInit) {
                    writer.addHeaderAlias(head.getFieldName(), head.getName());
                    headNameList.add(head.getFieldName());
                }
                if (!Arrays.asList(FieldEnum.AREA.getType(), FieldEnum.DETAIL_TABLE.getType()).contains(head.getType())) {
                    headMap.put(head.getFieldName(), head.getType());
                }
            }

            if (isInit) {
                // 取消数据的黑色边框以及数据左对齐
                CellStyle cellStyle = writer.getCellStyle();
                cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyle.setBorderTop(BorderStyle.NONE);
                cellStyle.setBorderBottom(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                Font defaultFont = writer.createFont();
                defaultFont.setFontHeightInPoints((short) 11);
                cellStyle.setFont(defaultFont);
                // 取消数字格式的数据的黑色边框以及数据左对齐
                CellStyle cellStyleForNumber = writer.getStyleSet().getCellStyleForNumber();
                cellStyleForNumber.setBorderTop(BorderStyle.NONE);
                cellStyleForNumber.setBorderBottom(BorderStyle.NONE);
                cellStyleForNumber.setBorderLeft(BorderStyle.NONE);
                cellStyleForNumber.setBorderRight(BorderStyle.NONE);
                cellStyleForNumber.setAlignment(HorizontalAlignment.LEFT);
                cellStyleForNumber.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyleForNumber.setFont(defaultFont);
                // 取消日期格式的数据的黑色边框以及数据左对齐
                CellStyle cellStyleForDate = writer.getStyleSet().getCellStyleForDate();
                cellStyleForDate.setBorderTop(BorderStyle.NONE);
                cellStyleForDate.setBorderBottom(BorderStyle.NONE);
                cellStyleForDate.setBorderLeft(BorderStyle.NONE);
                cellStyleForDate.setBorderRight(BorderStyle.NONE);
                cellStyleForDate.setAlignment(HorizontalAlignment.LEFT);
                cellStyleForDate.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyleForDate.setFont(defaultFont);
                //设置行高以及列宽
                writer.setRowHeight(-1, 20);
                writer.setColumnWidth(-1, 20);
                //只保留别名中的字段
                writer.setOnlyAlias(true);

                List<ExcelMergeEntity> mergeData = excelParseService.getMergeData();

                if (mergeData != null) {
                    for (ExcelMergeEntity mergeDatum : mergeData) {
                        int i = headNameList.indexOf(mergeDatum.getFieldName());
                        if (i < 0) {
                            continue;
                        }
                        writer.merge(mergeDatum.getFirstRow(), mergeDatum.getLastRow(), i, i, mergeDatum.getContent(), false);
                    }
                }

            }
            // 设置数据
            formatData(dataList, excelParseService, headMap);


            if (dataList.isEmpty()) {
                Map<String, Object> record = new HashMap<>();
                headList.forEach(head -> record.put(head.getFieldName(), ""));
                writer.write(Collections.singletonList(record), isInit);
            } else {
                writer.write(dataList, isInit);
            }


            CellStyle style = writer.getHeadCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            Font font = writer.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
        } catch (Exception e) {
            log.error("导出客户错误：", e);
        }
    }


    /**
     * 统一导出数据模板
     */
    public static void exportExcelCsv(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, File file) {
        CsvWriter writer;
        boolean isInit = false;
        if (THREAD_LOCAL.get() == null) {
            writer = CsvUtil.getWriter(file, Charset.defaultCharset());
            THREAD_LOCAL.set(writer);
            isInit = true;
        } else {
            writer = (CsvWriter) THREAD_LOCAL.get();
        }
        List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
        Map<String, Integer> headMap = new HashMap<>(headList.size(), 1.0f);
        List<Object> names = new ArrayList<>();
        List<String> fieldNames = new LinkedList<>();
        for (ExcelDataEntity head : headList) {
            if (isInit) {
                names.add(head.getName());
            }
            fieldNames.add(head.getFieldName());
            if (!Arrays.asList(FieldEnum.AREA.getType(), FieldEnum.CURRENT_POSITION.getType(), FieldEnum.DETAIL_TABLE.getType()).contains(head.getType())) {
                headMap.put(head.getFieldName(), head.getType());
            }
        }
        List<List<Object>> writerList = new ArrayList<>();
        if (!names.isEmpty()) {
            writerList.add(names);
        }
        // 设置数据
        formatData(dataList, excelParseService, headMap);
        for (Map<String, Object> data : dataList) {
            List<Object> values = new ArrayList<>();
            for (String fieldName : fieldNames) {
                values.add(data.get(fieldName));
            }
            writerList.add(values);
        }
        writer.write(writerList);
    }


    public static void formatData(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, Map<String, Integer> headMap) {
        Set<String> userList = new HashSet<>();
        Set<String> deptList = new HashSet<>();
        userList.add("createUserName");
        userList.add("ownerUserName");
        userList.add("realname");
        deptList.add("ownerDeptName");
        deptList.add("deptName");
        headMap.forEach((k, v) -> {
            if (FieldEnum.USER.getType().equals(v)) {
                userList.add(k);
            } else if (FieldEnum.STRUCTURE.getType().equals(v)) {
                deptList.add(k);
            }
        });
        for (Map<String, Object> record : dataList) {
            excelParseService.getFunc().call(record, headMap);
        }
    }

    private static final int TWO = 2;

    /**
     * 统一下载导入模板
     */
    public static void importExcel(ExcelParseService excelParseService, List<?> list, HttpServletResponse response, String module) {
        List<ExcelDataEntity> dataEntities = excelParseService.parseData(list, true);
        try (ExcelWriter writer = ExcelUtil.getWriter(excelParseService.isXlsx())) {
            //因为重复合并单元格会导致样式丢失，所以先获取全部字段一次合并
            int sum = dataEntities.stream().mapToInt(data -> excelParseService.addCell(null, 0, 0, data.getFieldName())).sum();
            int addressSum = dataEntities.stream().filter(e -> ObjectUtil.equal(e.getType(), FieldEnum.AREA_POSITION.getType())).mapToInt(e -> {
                switch (e.getPrecisions()) {
                    case 1:
                        return 3;
                    case 2:
                        return 2;
                    case 3:
                        return 1;
                    case 4:
                    default:
                        return 0;
                }
            }).sum();
            sum += addressSum;
            writer.renameSheet(excelParseService.getExcelName() + "导入模板");
            writer.merge(dataEntities.size() - 1 + sum, excelParseService.getMergeContent(module), true);
            writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);
            writer.getHeadCellStyle().setWrapText(true);
            Font headFont = writer.createFont();
            headFont.setFontHeightInPoints((short) 11);
            writer.getHeadCellStyle().setFont(headFont);
            writer.getHeadCellStyle().setFillPattern(FillPatternType.NO_FILL);
            if (!"subject".equals(module)) {
                writer.getOrCreateRow(0).setHeightInPoints(150);
            } else {
                writer.getOrCreateRow(0).setHeightInPoints(70);
            }
            writer.setRowHeight(-1, 20);
            //单选按钮字符超过255时的sheet的index
            Integer sheetIndex = 0;
            int crmFieldIndex = 0;
            //设置样式
            for (int i = 0, k = dataEntities.size(), z = 0; i < k; i++, z++) {
                ExcelDataEntity dataEntity = dataEntities.get(i);
                //会新增cell或者对当前cell做调整，直接跳过默认处理
                int n = excelParseService.addCell(writer, z, 1, dataEntity.getFieldName());
                if (n > 0) {
                    z += n;
                    continue;
                }
                CellStyle columnStyle = writer.getOrCreateColumnStyle(z);
                //设置统一字体
                columnStyle.setFont(headFont);
                DataFormat dateFormat = writer.getWorkbook().createDataFormat();
                if (Objects.equals(dataEntities.get(i).getType(), FieldEnum.DATE.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                } else if (Objects.equals(dataEntities.get(i).getType(), FieldEnum.DATETIME.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    columnStyle.setDataFormat(dateFormat.getFormat("@"));
                }
                writer.setColumnWidth(z, 20);
                Cell cell = writer.getOrCreateCell(z, 1);
                //必填字段的特殊处理
                if (Objects.equals(1, dataEntity.getIsNull())) {
                    cell.setCellValue("*" + dataEntity.getName());
                    CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                    Font cellFont = writer.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setColor(Font.COLOR_RED);
                    cellStyle.setFont(cellFont);
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellValue(dataEntity.getName());
                }
                if (FieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    dataEntity.setSetting(Arrays.asList("一星", "二星", "三星", "四星", "五星"));
                }
                //选择类型增加下拉框
                if (CollUtil.isNotEmpty(dataEntity.getSetting()) && !ObjectUtil.equal(dataEntity.getType(), FieldEnum.CHECKBOX.getType())) {
                    String[] array = dataEntity.getSetting().stream().map(data -> {
                        if (data instanceof JSONObject && ((JSONObject) data).containsKey("name")) {
                            return ((JSONObject) data).getString("name");
                        }
                        return data.toString();
                    }).toArray(String[]::new);
                    CellRangeAddressList addressList = new CellRangeAddressList(2, 10002, z, z);
                    // array的字符总长度
                    int arrayLength = Arrays.stream(array).mapToInt(String::length).sum();
                    //中文字符串占用长度和ASCII长度不一致，所以此处不判断为255
                    if (arrayLength >= 120) {
                        sheetIndex++;
                        Workbook workbook = writer.getWorkbook();
                        String sheetName = "setting" + sheetIndex;
                        //将下拉框数据放到新的sheet里，然后excel通过新的sheet数据加载下拉框数据
                        Sheet sheet = workbook.createSheet(sheetName);

                        // 创建名称，可被其他单元格引用
                        Name namedCell = workbook.createName();
                        namedCell.setNameName(sheetName);
                        namedCell.setRefersToFormula(sheetName + "!$A$1:$A$" + 1000);

                        //创建单元格对象
                        Cell cellSetting = null;
                        //遍历我们上面的数组，将数据取出来放到新sheet的单元格中
                        for (int l = 0, length = array.length; l < length; l++) {
                            //取出数组中的每个元素
                            String name = array[l];
                            //根据i创建相应的行对象（说明我们将会把每个元素单独放一行）
                            Row row = sheet.createRow(l);
                            //创建每一行中的第一个单元格
                            cellSetting = row.createCell(0);
                            //然后将数组中的元素赋值给这个单元格
                            cellSetting.setCellValue(name);
                        }
                        //加载数据,将sheet中的数据转换为List形式
                        DVConstraint constraint = DVConstraint.createFormulaListConstraint(sheetName);
                        // 将设置下拉选的位置和数据的对应关系 绑定到一起
                        DataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
                        //将当前sheet设置为隐藏
                        workbook.setSheetHidden(sheetIndex, true);
                        //将数据赋给下拉列表
                        workbook.getSheetAt(0).addValidationData(dataValidation);
                    } else {
                        writer.addSelect(addressList, array);
                    }
                } else if (FieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), "一星", "二星", "三星", "四星", "五星");
                }


                // 省市区
                if (ObjectUtil.equal(dataEntity.getType(), FieldEnum.AREA_POSITION.getType())) {
                    crmFieldIndex++;
                    Workbook wb = writer.getWorkbook();
                    Sheet sheet = writer.getSheet();
                    String fieldName = dataEntity.fieldName;
                    int four = 4;
                    for (int j = 0; j < four; j++) {
                        writer.setColumnWidth(z + j, 20);
                    }
                    Sheet hideSheet = wb.createSheet(fieldName);
                    wb.setSheetHidden(wb.getSheetIndex(hideSheet), true);
                    int rowId = 0;
                    // 这个addressIndex是需要在省市区结束后给excel的x向右移动几位
                    int addressIndex = 0;

                    if (dataEntity.getPrecisions() <= 4) {
                        Cell cell1 = writer.getOrCreateCell(z, 1);
                        if (Objects.equals(1, dataEntity.getIsNull())) {
                            cell1.setCellValue("*" + dataEntity.getName() + "-省");
                            CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                            Font cellFont = writer.createFont();
                            cellFont.setFontHeightInPoints((short) 11);
                            cellFont.setColor(Font.COLOR_RED);
                            cellStyle.setFont(cellFont);
                            cell1.setCellStyle(cellStyle);
                        } else {
                            cell1.setCellValue(dataEntity.getName() + "-省");
                        }
                    }
                    if (dataEntity.getPrecisions() <= 3) {
                        Cell cell2 = writer.getOrCreateCell(z + 1, 1);
                        cell2.setCellValue(dataEntity.getName() + "-市");
                        addressIndex++;
                    }
                    if (dataEntity.getPrecisions() <= 2) {
                        Cell cell3 = writer.getOrCreateCell(z + 2, 1);
                        cell3.setCellValue(dataEntity.getName() + "-区");
                        addressIndex++;
                    }
                    if (dataEntity.getPrecisions() <= 1) {
                        Cell cell4 = writer.getOrCreateCell(z + 3, 1);
                        cell4.setCellValue(dataEntity.getName() + "-详细地址");
                        addressIndex++;
                    }
                    // 设置第一行，存省的信息
                    Row provinceRow = hideSheet.createRow(rowId++);
                    provinceRow.createCell(0).setCellValue(dataEntity.getName() + "-省列表");
                    String[] provinceList = CrmExcelUtil.getProvinceArray();
                    for (int line = 0; line < provinceList.length; line++) {
                        Cell provinceCell = provinceRow.createCell(line + 1);
                        provinceCell.setCellValue(provinceList[line]);
                    }

                    // 只执行一次
                    if (crmFieldIndex == 1) {
                        // 将具体的数据写入到每一行中，行开头为父级区域，后面是子区域。
                        Map<String, List<String>> areaMap = CrmExcelUtil.getAreaMap();
                        for (String key : areaMap.keySet()) {
                            List<String> son = areaMap.get(key);
                            Row subRow = hideSheet.createRow(rowId++);
                            subRow.createCell(0).setCellValue(key);
                            for (int line = 0; line < son.size(); line++) {
                                Cell cellTemp = subRow.createCell(line + 1);
                                cellTemp.setCellValue(son.get(line));
                            }
                            // 添加名称管理器
                            String range = CrmExcelUtil.getRange(1, rowId, son.size());
                            Name name = wb.createName();
                            // key不可重复
                            name.setNameName(key);
                            String formula = fieldName + "!" + range;
                            name.setRefersToFormula(formula);
                        }
                    }

                    // 省级下拉框
                    CellRangeAddressList provRangeAddressList = new CellRangeAddressList(2, 10004, z, z);
                    DataValidationHelper validationHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(provinceList);
                    //设置下拉框数据
                    DataValidation dataValidation = validationHelper.createValidation(constraint, provRangeAddressList);
                    dataValidation.createErrorBox("error", "请选择正确的省份");
                    sheet.addValidationData(dataValidation);

                    //市 区下拉框
                    int forIndex = 10004;
                    for (int line = TWO; line < forIndex; line++) {
                        if (dataEntity.getPrecisions() <= 3) {
                            CrmExcelUtil.setDataValidation(CrmExcelUtil.getCorrespondingLabel(z + 1), sheet, line, z + 1);
                        }
                        if (dataEntity.getPrecisions() <= 2) {
                            CrmExcelUtil.setDataValidation(CrmExcelUtil.getCorrespondingLabel(z + 2), sheet, line, z + 2);
                        }
                    }

                    z += addressIndex;
                }
            }
            //自定义标题别名
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "导入模板", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("下载" + excelParseService.getExcelName() + "导入模板错误", e);
        }

    }


    /**
     * 导出审批excel
     *
     * @param list
     * @param exportFieldList
     */
    public static void exportApprovalExcel(List<Map<String, Object>> list, String fileName, List<Map<String, Object>> exportFieldList, HttpServletResponse response) {

        //集合key
        List<String> listName = new ArrayList<>();

        List<Map<String, Object>> newData = new LinkedList<>();

        List<Map<String, Object>> dataMap = new LinkedList<>();

        ExcelWriter writer = ExcelUtil.getWriter();

        //判断导出数据是否为空，如果为空导出标题就前端传的数据为准
        if (ObjectUtil.isEmpty(list)) {
            exportFieldList = exportFieldList.stream().sorted(Comparator.comparing(map -> Integer.valueOf(map.get("sort").toString()))).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            for (Map<String, Object> objectMap : exportFieldList) {

                String name = String.valueOf(objectMap.get("name"));
                writer.addHeaderAlias(name, name);
                map.put(name, "");
            }
            dataMap.add(map);
        } else {

            //不需要导出的字段类型
            List<Integer> excludeFieldId = new ArrayList<Integer>() {{
                add(FieldEnum.ATTACHMENT.getType());
                add(FieldEnum.HANDWRITING_SIGN.getType());
                add(FieldEnum.FILE.getType());
                add(FieldEnum.FIELD_GROUP.getType());
            }};

            //获取需要导出的fieldKey
            List<String> fieldKey = exportFieldList.stream().map(e -> String.valueOf(e.get("fieldKey"))).collect(Collectors.toList());

            //数据处理
            for (Map<String, Object> data : list) {

                List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");

                fieldList = fieldList.stream().filter(e -> fieldKey.contains(e.get("fieldName").toString())).collect(Collectors.toList());

                Map<String, Object> objectMap = new LinkedHashMap<>();
                for (Map<String, Object> map : fieldList) {
                    Object name = map.get("name");

                    Integer type = Integer.valueOf(String.valueOf(map.get("type")));

                    if (excludeFieldId.contains(type)) {
                        continue;
                    }

                    Object value = map.get("value");
                    List<Map<String, Object>> childList = new LinkedList<>();
                    switch (type) {
                        case 22: {
                            listName.add(name + "");
                            Map<String, Object> chilData = new LinkedHashMap<>();
                            chilData.put("交通工具", " ");
                            chilData.put("单程返程", " ");
                            chilData.put("出发城市", " ");
                            chilData.put("目的城市", " ");
                            chilData.put("开始时间", " ");
                            chilData.put("结束时间", " ");
                            chilData.put("时长", " ");
                            chilData.put("备注", " ");
                            childList.add(chilData);

                            if (ObjectUtil.isNotEmpty(value)) {
                                childList.remove(0);
                                JSONArray objects = JSONUtil.parseArray(value);
                                for (Object object : objects) {
                                    OaExamineTravel oaExamineTravel = JSONUtil.toBean(object.toString(), OaExamineTravel.class);
                                    Map<String, Object> chilData1 = new LinkedHashMap<>();
                                    chilData1.put("交通工具", oaExamineTravel.getVehicle());
                                    chilData1.put("单程返程", oaExamineTravel.getTrip());
                                    chilData1.put("出发城市", oaExamineTravel.getStartAddress());
                                    chilData1.put("目的城市", oaExamineTravel.getEndAddress());
                                    chilData1.put("开始时间", oaExamineTravel.getStartTime());
                                    chilData1.put("结束时间", oaExamineTravel.getEndTime());
                                    chilData1.put("时长", oaExamineTravel.getDuration());

                                    chilData1.put("备注", oaExamineTravel.getDescription());

                                    childList.add(chilData1);
                                }
                            }
                            objectMap.put(name + "", childList);
                            break;
                        }
                        case 23: {
                            listName.add(name + "");
                            Map<String, Object> chilData = new LinkedHashMap<>();
                            chilData.put("出发城市", " ");
                            chilData.put("目的城市", " ");
                            chilData.put("开始时间", " ");
                            chilData.put("结束时间", " ");
                            chilData.put("交通费用", " ");
                            chilData.put("住宿费用", " ");
                            chilData.put("餐饮费用", " ");
                            chilData.put("其他费用", " ");
                            chilData.put("费用明细描述", " ");
                            chilData.put("发票数量", " ");
                            childList.add(chilData);
                            if (ObjectUtil.isNotEmpty(value)) {
                                childList.remove(0);
                                JSONArray objects = JSONUtil.parseArray(value);
                                for (Object object : objects) {
                                    Map<String, Object> chilData1 = new LinkedHashMap<>();
                                    OaExamineTravel oaExamineTravel = JSONUtil.toBean(object.toString(), OaExamineTravel.class);
                                    chilData1.put("出发城市", oaExamineTravel.getStartAddress());
                                    chilData1.put("目的城市", oaExamineTravel.getEndAddress());
                                    chilData1.put("开始时间", oaExamineTravel.getStartTime());
                                    chilData1.put("结束时间", oaExamineTravel.getEndTime());
                                    chilData1.put("交通费用", oaExamineTravel.getTraffic());
                                    chilData1.put("住宿费用", oaExamineTravel.getStay());
                                    chilData1.put("餐饮费用", oaExamineTravel.getDiet());
                                    chilData1.put("其他费用", oaExamineTravel.getOther());
                                    chilData1.put("费用明细描述", oaExamineTravel.getDescription());
                                    chilData1.put("发票数量", ObjectUtil.isNotEmpty(oaExamineTravel.getFile()) ? oaExamineTravel.getFile().size() : 0);
                                    childList.add(chilData1);
                                }
                            }
                            objectMap.put(name + "", childList);
                            break;
                        }
                        case 45: {
                            listName.add(name + "");
                            if (ObjectUtil.isNotEmpty(value)) {
                                JSONArray objects = JSONUtil.parseArray(value);


                                Object fieldExtendList = map.get("fieldExtendList");

                                JSONArray jsonArray = JSONUtil.parseArray(fieldExtendList);
                                Map<String, Object> chilData = new LinkedHashMap<>();

                                List<String> fieldNames = new ArrayList<>();
                                for (Object o : jsonArray) {
                                    cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(o);
                                    fieldNames.add(jsonObject.get("fieldName").toString());
                                    chilData.put(jsonObject.get("name").toString(), "");
                                }

                                for (Object object : objects) {

                                    Map<String, Object> map1 = ObjectUtil.cloneByStream(chilData);

                                    if (JSONUtil.isTypeJSONArray(object.toString())) {
                                        JSONArray objects1 = JSONUtil.parseArray(object);
                                        for (Object o : objects1) {
                                            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(o);
                                            Object value1 = jsonObject.get("value");
                                            String fieldName = jsonObject.get("fieldName").toString();
                                            Integer type1 = jsonObject.getInt("type");
                                            if (excludeFieldId.contains(type1)) {
                                                continue;
                                            }
                                            if (fieldNames.contains(fieldName)) {
                                                value1 = specialField(type1, value1);
                                                map1.put(jsonObject.get("name").toString(), value1);
                                            }
                                        }
                                    }
                                    childList.add(map1);
                                }
                            } else {
                                Object fieldExtendList = map.get("fieldExtendList");
                                JSONArray objects = JSONUtil.parseArray(fieldExtendList);

                                Map<String, Object> chilData = new LinkedHashMap<>();
                                for (Object object : objects) {
                                    cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(object);
                                    Integer type1 = Integer.valueOf(jsonObject.get("type") + "");
                                    if (excludeFieldId.contains(type1)) {
                                        continue;
                                    }
                                    chilData.put(jsonObject.get("name").toString(), " ");
                                }
                                childList.add(chilData);

                            }
                            objectMap.put(name + "", childList);
                            break;
                        }

                        default: {
                            String specialField = specialField(type, value);
                            objectMap.put(name + "", specialField);
                        }
                    }
                }

                if (fieldKey.contains("customerNames")) {
                    objectMap.put("相关客户", data.get("customerNames"));
                }
                if (fieldKey.contains("businessNames")) {
                    objectMap.put("相关商机", data.get("businessNames"));
                }
                if (fieldKey.contains("contactsNames")) {
                    objectMap.put("相关联系人", data.get("contactsNames"));
                }
                if (fieldKey.contains("contractNames")) {
                    objectMap.put("相关合同", data.get("contractNames"));
                }
                if (fieldKey.contains("receivablesNames")) {
                    objectMap.put("相关回款", data.get("receivablesNames"));
                }
                if (fieldKey.contains("productNames")) {
                    objectMap.put("相关产品", data.get("productNames"));
                }
                if (fieldKey.contains("examineStatus")) {
                    objectMap.put("当前流程状态", data.get("examineStatus"));
                }


                newData.add(objectMap);
            }


            dataMap = new LinkedList<>(newData);

            /*
             * 判断导出数据中是否有数组数据，如果没有直接导出
             * 有的话数据平铺展开，并且设置合并单元格信息
             */
            if (ObjectUtil.isNotEmpty(listName)) {
                dataMap = new ArrayList<>();
                for (Map<String, Object> objectMap : newData) {

                    String s1 = listName.stream().max(Comparator.comparingInt(o -> getSize(objectMap.get(o)))).get();
                    int size = getSize(objectMap.get(s1));
                    for (String name : listName) {
                        Object o = objectMap.get(name);
                        if (o instanceof Collection) {
                            if (((Collection<?>) o).size() == 0) {

                                continue;
                            }
                            int diff = size - ((Collection<?>) o).size();
                            Map<String, Object> next = (Map<String, Object>) ((Collection<?>) o).iterator().next();
                            for (int i = 0; i < diff; i++) {
                                Map<String, Object> nextMap = new HashMap<>(next.size());
                                for (String s2 : next.keySet()) {
                                    nextMap.put(s2, "");
                                }
                                ((Collection<Map>) o).add(nextMap);
                            }
                        }
                    }

                }
                List<Integer> sizeList = new LinkedList<>();
                for (Map<String, Object> objectMap : newData) {
                    int size = getSize(objectMap.get(listName.get(0)));
                    sizeList.add(size);
                    for (int i = 0; i < size; i++) {
                        Map<String, Object> newMap = new LinkedHashMap<>(objectMap);
                        for (String s : listName) {
                            newMap.remove(s);
                        }
                        for (String s : listName) {
                            List<Map<String, Object>> o = (List<Map<String, Object>>) objectMap.get(s);
                            if (ObjectUtil.isNotEmpty(o)) {
                                Map<String, Object> map = o.get(i);
                                for (String s1 : map.keySet()) {
                                    newMap.put(s + "-" + s1, map.get(s1));
                                }
                            }
                        }
                        dataMap.add(newMap);
                    }
                }


                int lastRow = 0;
                for (Integer integer : sizeList) {
                    if (integer != 1) {
                        int column = 0;
                        Map<String, Object> objectMap = dataMap.get(lastRow);
                        for (String key : objectMap.keySet()) {
                            if (key.contains("-")) {
                                continue;
                            }
                            writer.merge(lastRow + 1, lastRow + integer, column, column, objectMap.get(key), false);
                            column++;
                        }
                    }
                    lastRow += integer;
                }
            }

        }


        CellStyle cellStyle = writer.getCellStyle();
        cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        Font defaultFont = writer.createFont();
        defaultFont.setFontHeightInPoints((short) 11);
        cellStyle.setFont(defaultFont);
        // 取消数字格式的数据的黑色边框以及数据左对齐
        CellStyle cellStyleForNumber = writer.getStyleSet().getCellStyleForNumber();
        cellStyleForNumber.setBorderTop(BorderStyle.NONE);
        cellStyleForNumber.setBorderBottom(BorderStyle.NONE);
        cellStyleForNumber.setBorderLeft(BorderStyle.NONE);
        cellStyleForNumber.setBorderRight(BorderStyle.NONE);
        cellStyleForNumber.setAlignment(HorizontalAlignment.LEFT);
        cellStyleForNumber.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        cellStyleForNumber.setFont(defaultFont);
        // 取消日期格式的数据的黑色边框以及数据左对齐
        CellStyle cellStyleForDate = writer.getStyleSet().getCellStyleForDate();
        cellStyleForDate.setBorderTop(BorderStyle.NONE);
        cellStyleForDate.setBorderBottom(BorderStyle.NONE);
        cellStyleForDate.setBorderLeft(BorderStyle.NONE);
        cellStyleForDate.setBorderRight(BorderStyle.NONE);
        cellStyleForDate.setAlignment(HorizontalAlignment.LEFT);
        cellStyleForDate.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        cellStyleForDate.setFont(defaultFont);
        //设置行高以及列宽
        writer.setRowHeight(-1, 20);
        writer.setColumnWidth(-1, 20);
        writer.write(dataMap, true);

        writer.setOnlyAlias(true);

        CellStyle style = writer.getHeadCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        Font font = writer.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);


        ServletOutputStream out = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8") + ".xls");

            out = response.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);

    }


    @Data
    public static class ExcelDataEntity {

        /* 字段名称 */
        private String fieldName;

        /* 展示名称 */
        private String name;

        /* 字段类型 */
        private Integer type;

        /* 是否必填 1 是 0 否 */
        private Integer isNull;

        /* 设置列表 */
        private List<Object> setting;

        /* 精度，允许的最大小数位/地图精度/明细表格、逻辑表单展示方式 */
        private Integer precisions;

        public ExcelDataEntity() {
        }

        public ExcelDataEntity(String fieldName, String name, Integer type) {
            this.fieldName = fieldName;
            this.name = name;
            this.type = type;
        }
    }


    @Data
    public static class ExcelMergeEntity {
        private Integer firstRow;

        private Integer lastRow;

        private Object content;

        private String fieldName;
    }


    public static abstract class ExcelParseService {


        /**
         * 设置自定义数据处理方法
         *
         * @return func
         */
        public DataFunc getFunc() {
            return (record, headMap) -> {
            };
        }

        public List<ExcelMergeEntity> getMergeData() {
            return null;
        }


        /**
         * 统一处理数据
         *
         * @param list        请求头数据
         * @param importExcel 是否是导入模板
         * @return 转化后的请求头数据
         */
        public List<ExcelDataEntity> parseData(List<?> list, boolean importExcel) {
            List<ExcelDataEntity> entities = list.stream().map(obj -> {
                if (obj instanceof ExcelDataEntity) {
                    return (ExcelDataEntity) obj;
                }
                return BeanUtil.copyProperties(obj, ExcelDataEntity.class);
            }).collect(Collectors.toList());
            if (importExcel) {
                entities.removeIf(head -> ExcelParseUtil.removeFieldByType(head.getType()));
            } else {
                entities.removeIf(head -> FieldEnum.HANDWRITING_SIGN.getType().equals(head.getType()));
            }
            entities.forEach(data -> {
                if (ObjectUtil.equal(data.getType(), FieldEnum.TAG.getType())) {
                    data.setSetting(new ArrayList<>());
                }
            });
            return entities;
        }


        /**
         * 如果需要执行全部数据导出，分批次的获取数据
         *
         * @return data
         */
        public List<? extends Map<String, Object>> getNextData() {
            return null;
        }

        /**
         * 获取excel表格名称
         *
         * @return 表格名称
         */
        public abstract String getExcelName();

        /**
         * 导入的时候需要的可能需要新增字段场景
         *
         * @param writer    writer
         * @param x         – X坐标，从0计数，即列号
         * @param y         – Y坐标，从0计数，即行号
         * @param fieldName 字段名称
         * @return 新增的行数
         */
        public int addCell(ExcelWriter writer, Integer x, Integer y, String fieldName) {
            return 0;
        }

        /**
         * 是否是xlsx格式，xlsx导出会比xlx3倍左右，谨慎使用
         *
         * @return isXlsx
         */
        public boolean isXlsx() {
            return false;
        }

        /**
         * 获取合并内容，需要修改时重写此方法
         *
         * @param module 模块名称
         * @return content
         */
        public String getMergeContent(String module) {
            return "注意事项：\n" +
                    "1、表头标“*”的红色字体为必填项\n" +
                    "2、日期时间：推荐格式为2020-02-02 13:13:13\n" +
                    "3、日期：推荐格式为2020-02-02\n" +
                    "4、手机号：支持6-15位数字（包含国外手机号格式）\n" +
                    "5、邮箱：只支持邮箱格式\n" +
                    "6、多行文本：字数限制为800字\n" +
                    "7、标签字段：如果需要导入多个标签，标签之间用英文逗号隔开\n" +
                    "8、创建时间：非必填项，如果不填写则默认为导入时间";
        }


    }

    /**
     * 数据格式化方法
     */
    @FunctionalInterface
    public interface DataFunc {
        /**
         * 数据格式化方法
         *
         * @param record  记录
         * @param headMap 标题
         */
        void call(Map<String, Object> record, Map<String, Integer> headMap);
    }

    /**
     * 不支持导入的字段
     */
    private static final List<Integer> TYPE_LIST = Arrays.asList(FieldEnum.FILE.getType(), FieldEnum.USER.getType(), FieldEnum.STRUCTURE.getType(), FieldEnum.AREA.getType(), FieldEnum.CURRENT_POSITION.getType(), FieldEnum.DATE_INTERVAL.getType(), FieldEnum.BOOLEAN_VALUE.getType(), FieldEnum.HANDWRITING_SIGN.getType(), FieldEnum.DESC_TEXT.getType(), FieldEnum.DETAIL_TABLE.getType(), FieldEnum.CALCULATION_FUNCTION.getType(), FieldEnum.FIELD_GROUP.getType(), FieldEnum.SERIAL_NUMBER.getType(), FieldEnum.ATTENTION.getType());


    /**
     * 删除不支持导入的字段
     *
     * @return true为不支持导入
     */
    public static boolean removeFieldByType(Integer type) {
        return TYPE_LIST.contains(type);
    }

    public static ExcelDataEntity toEntity(String fieldName, String name) {
        return new ExcelDataEntity(fieldName, name, FieldEnum.TEXT.getType());
    }

    public static ExcelDataEntity toEntity(String fieldName, String name, Integer type) {
        return new ExcelDataEntity(fieldName, name, type);
    }


    /**
     * 导出数据value处理
     *
     * @param type  字段类型
     * @param value 字段的值
     * @return data
     */
    public static String specialField(Integer type, Object value) {

        if (ObjectUtil.isEmpty(value)) {
            return "";
        } else {
            String returnString = "";
            switch (type) {
                case 10: {
                    returnString = parseDataToString(value, "realname");
                    break;
                }
                case 61:
                case 43:
                case 12:
                    returnString = parseDataToString(value, "name");
                    break;
                case 41: {
                    //布尔值
                    returnString = BooleanUtil.toBoolean(value.toString()) ? "是" : "否";
                    break;
                }
                case 44:
                    //定位
                    returnString = JSONUtil.parseObj(value).get("address") + "";
                    break;
                case 70: {
                    //富文本
                    returnString = HtmlUtil.cleanHtmlTag(value + "");
                    returnString = HtmlUtil.unescape(returnString);
                    break;
                }
                default: {
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof Collection) {
                            returnString = ((Collection<?>) value).stream().map(StrUtil::toString).collect(Collectors.joining(","));
                        } else {
                            returnString = StrUtil.toString(value);
                        }
                    }
                    break;
                }
            }
            return returnString;
        }
    }


    private static int getSize(Object o) {
        if (o instanceof Collection) {
            return ((Collection<?>) o).size();
        }
        return 0;
    }

    private static String parseDataToString(Object value, String key) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).stream().map(data -> {
                if (data instanceof Map) {
                    return MapProxy.create((Map<?, ?>) data).getStr(key);
                }
                return "";
            }).collect(Collectors.joining(","));
        } else if (value instanceof Map<?, ?>) {
            return MapProxy.create((Map<?, ?>) value).getStr(key);
        }
        return "";
    }
}
