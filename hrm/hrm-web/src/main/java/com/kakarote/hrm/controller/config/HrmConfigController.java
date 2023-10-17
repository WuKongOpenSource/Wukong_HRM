package com.kakarote.hrm.controller.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.log.annotation.OperateLog;
import com.kakarote.common.log.entity.OperationLog;
import com.kakarote.common.log.entity.OperationResult;
import com.kakarote.common.log.enums.ApplyEnum;
import com.kakarote.common.log.enums.BehaviorEnum;
import com.kakarote.common.log.enums.OperateObjectEnum;
import com.kakarote.common.log.enums.OperateTypeEnum;
import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.constant.ConfigType;
import com.kakarote.hrm.constant.LabelGroupEnum;
import com.kakarote.hrm.entity.BO.AddEmployeeFieldBO;
import com.kakarote.hrm.entity.BO.AddInsuranceSchemeBO;
import com.kakarote.hrm.entity.BO.DeleteRecruitChannelBO;
import com.kakarote.hrm.entity.BO.SetAchievementTableBO;
import com.kakarote.hrm.entity.PO.*;
import com.kakarote.hrm.entity.VO.*;
import com.kakarote.hrm.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hrmConfig")
@Api(tags = "人力资源后台配置接口")
public class HrmConfigController {

    @Autowired
    private IHrmConfigService configService;

    @Autowired
    private IHrmEmployeeFieldService employeeFieldService;

    @Autowired
    private IHrmInsuranceSchemeService insuranceSchemeService;

    @Autowired
    private IHrmAchievementTableService achievementTableService;

    @Autowired
    private IHrmRecruitChannelService recruitChannelService;

    @Autowired
    private IHrmEmployeeService employeeService;

    @Autowired
    private IHrmRecruitCandidateService recruitCandidateService;


    /**
     * --------------招聘渠道---------------
     */

    @PostMapping("/saveRecruitChannel")
    @ApiOperation("保存招聘渠道")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_BUSINESS_PARAM_SETTING, behavior = BehaviorEnum.UPDATE)
    public Result saveRecruitChannel(@RequestBody List<HrmRecruitChannel> channelList) {
        recruitChannelService.saveOrUpdateBatch(channelList);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("招聘渠道");
        operationLog.setOperationInfo("编辑招聘渠道");
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/queryRecruitChannelList")
    @ApiOperation("查询招聘渠道配置列表")
    public Result<List<HrmRecruitChannel>> queryRecruitChannelList() {
        List<HrmRecruitChannel> list = recruitChannelService.list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HrmRecruitChannel channel : list) {
                //添加语言包key
                channel.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("value_resourceKey", "admin.recruitChannel.", channel.getValue()));
            }
        }
        return Result.ok(list);
    }

    @PostMapping("/deleteRecruitChannel")
    @ApiOperation("删除招聘渠道")
    public Result deleteRecruitChannel(@RequestBody DeleteRecruitChannelBO deleteRecruitChannelBO) {
        employeeService.lambdaUpdate()
                .set(HrmEmployee::getChannelId, deleteRecruitChannelBO.getChangeChannelId())
                .eq(HrmEmployee::getChannelId, deleteRecruitChannelBO.getDeleteChannelId())
                .update();
        recruitCandidateService.lambdaUpdate()
                .set(HrmRecruitCandidate::getChannelId, deleteRecruitChannelBO.getChangeChannelId())
                .eq(HrmRecruitCandidate::getChannelId, deleteRecruitChannelBO.getDeleteChannelId())
                .update();
        recruitChannelService.removeById(deleteRecruitChannelBO.getDeleteChannelId());
        return Result.ok();
    }

    /**
     * --------------淘汰原因---------------
     */
    @PostMapping("/saveRecruitEliminate")
    @ApiOperation("保存淘汰原因")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_BUSINESS_PARAM_SETTING, behavior = BehaviorEnum.UPDATE)
    public Result saveRecruitEliminate(@RequestBody List<String> data) {
        configService.lambdaUpdate().eq(HrmConfig::getType, ConfigType.ELIMINATION_REASONS.getValue()).remove();
        List<HrmConfig> collect = data.stream().map(value -> {
            HrmConfig hrmConfig = new HrmConfig();
            hrmConfig.setType(ConfigType.ELIMINATION_REASONS.getValue());
            hrmConfig.setValue(value);
            return hrmConfig;
        }).collect(Collectors.toList());
        configService.saveBatch(collect);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject("淘汰原因");
        operationLog.setOperationInfo("编辑淘汰原因");
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/queryRecruitEliminateList")
    @ApiOperation("查询淘汰原因")
    public Result<RecruitEliminateVO> queryRecruitEliminateList() {
        RecruitEliminateVO eliminateVO = new RecruitEliminateVO();
        List<String> list = configService.lambdaQuery().eq(HrmConfig::getType, ConfigType.ELIMINATION_REASONS.getValue()).list()
                .stream().map(HrmConfig::getValue).collect(Collectors.toList());
        Map<String, String> keyMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (StrUtil.isNotBlank(list.get(i))) {
                    //添加语言包key
                    keyMap.putAll(LanguageFieldUtil.getFieldNameKeyMap(StrUtil.toString(i), "hrm.config.1.", list.get(i)));
                }
            }
            eliminateVO.setLanguageKeyMap(keyMap);
        }
        eliminateVO.setRecruit(list);
        eliminateVO.setLanguageKeyMap(keyMap);
        return Result.ok(eliminateVO);
    }


    /**
     * --------------自定义字段---------------
     */
    @PostMapping("/queryFields")
    @ApiOperation("查询后台配置自定义字段列表")
    public Result<List<FiledListVO>> queryFields() {
        List<FiledListVO> fieldList = employeeFieldService.queryFields();
        return Result.ok(fieldList);
    }


    @PostMapping("/queryFieldByLabel/{labelGroup}")
    @ApiOperation("查询后台配置自定义字段列表")
    public Result<List<List<HrmEmployeeField>>> queryFieldByLabel(@ApiParam("1 个人信息 2 通讯信息 7 联系人信息 11 岗位信息") @PathVariable("labelGroup") Integer labelGroup) {
        List<List<HrmEmployeeField>> data = employeeFieldService.queryFieldByLabel(labelGroup);
        return Result.ok(data);
    }

    @PostMapping("/saveField")
    @ApiOperation("保存后台自定义字段")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, behavior = BehaviorEnum.UPDATE, object = OperateObjectEnum.HUMAN_MANAGEMENT_CUSTOM_FIELD)
    public Result saveField(@RequestBody AddEmployeeFieldBO addEmployeeFieldBO) {
        employeeFieldService.saveField(addEmployeeFieldBO);

        LabelGroupEnum parse = LabelGroupEnum.parse(addEmployeeFieldBO.getLabelGroup());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationObject(parse.getName());
        operationLog.setOperationInfo("修改了" + parse.getName() + "自定义字段");
        return OperationResult.ok(operationLog);
    }

    /**
     * --------------社保方案---------------
     */
    @PostMapping("/addInsuranceScheme")
    @ApiOperation("添加社保方案")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_INSURANCE_SCHEME_SETTING)
    public Result addInsuranceScheme(@Valid @RequestBody AddInsuranceSchemeBO addInsuranceSchemeBO) {
        OperationLog operationLog = insuranceSchemeService.setInsuranceScheme(addInsuranceSchemeBO);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/setInsuranceScheme")
    @ApiOperation("修改社保方案")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_INSURANCE_SCHEME_SETTING)
    public Result setInsuranceScheme(@Valid @RequestBody AddInsuranceSchemeBO addInsuranceSchemeBO) {
        OperationLog operationLog = insuranceSchemeService.setInsuranceScheme(addInsuranceSchemeBO);
        return OperationResult.ok(operationLog);
    }


    @PostMapping("/deleteInsuranceScheme/{schemeId}")
    @ApiOperation("删除社保方案")
    @OperateLog(apply = ApplyEnum.HUMAN_RESOURCE_MANAGEMENT, type = OperateTypeEnum.SETTING, object = OperateObjectEnum.HUMAN_INSURANCE_SCHEME_SETTING, behavior = BehaviorEnum.DELETE)
    public Result deleteInsuranceScheme(@PathVariable("schemeId") Long schemeId) {
        OperationLog operationLog = insuranceSchemeService.deleteInsuranceScheme(schemeId);
        return OperationResult.ok(operationLog);
    }

    @PostMapping("/queryInsuranceSchemePageList")
    @ApiOperation("查询参保方案列表")
    public Result<BasePage<InsuranceSchemeListVO>> queryInsuranceSchemePageList(@RequestBody PageEntity pageEntity) {
        BasePage<InsuranceSchemeListVO> page = insuranceSchemeService.queryInsuranceSchemePageList(pageEntity);
        return Result.ok(page);
    }

    @PostMapping("/queryInsuranceSchemeById/{schemeId}")
    @ApiOperation("查询参保方案详情")
    public Result<InsuranceSchemeVO> queryInsuranceSchemeById(@PathVariable("schemeId") Long schemeId) {
        InsuranceSchemeVO insuranceSchemeVO = insuranceSchemeService.queryInsuranceSchemeById(schemeId);
        return Result.ok(insuranceSchemeVO);
    }

    /**
     * --------------考核模板---------------
     */
    @PostMapping("/setAchievementTable")
    @ApiOperation("添加或修改考核模板考核模板")
    public Result<HrmAchievementTable> setAchievementTable(@Valid @RequestBody SetAchievementTableBO setAchievementTableBO) {
        HrmAchievementTable achievementTable = achievementTableService.setAchievementTable(setAchievementTableBO);
        return Result.ok(achievementTable);
    }


    @PostMapping("/queryAchievementTableById/{tableId}")
    @ApiOperation("根据类型查询考核模板")
    public Result<AchievementTableVO> queryAchievementTableById(@PathVariable("tableId") Long tableId) {
        AchievementTableVO achievementTableVO = achievementTableService.queryAchievementTableById(tableId);
        return Result.ok(achievementTableVO);
    }


    @PostMapping("/queryAchievementTableList")
    @ApiOperation("查询考核模板列表")
    public Result<List<HrmAchievementTable>> queryAchievementTableList() {
        List<HrmAchievementTable> list = achievementTableService.queryAchievementTableList();
        return Result.ok(list);
    }

}
