package com.kakarote.hrm.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.constant.ConfigType;
import com.kakarote.hrm.entity.PO.HrmConfig;
import com.kakarote.hrm.entity.VO.RecruitEliminateVO;
import com.kakarote.hrm.service.IHrmConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 淘汰原因表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-02
 */
@RestController
@RequestMapping("/hrmRecruitEliminate")
@Api(tags = "查询淘汰原因列表(表单使用)")
public class HrmRecruitEliminateController {

    @Autowired
    private IHrmConfigService configService;

    @PostMapping("/queryRecruitEliminateList")
    @ApiOperation("查询淘汰原因列表")
    public Result<RecruitEliminateVO> queryRecruitEliminateList() {
        RecruitEliminateVO eliminateVO = new RecruitEliminateVO();
        List<String> list = configService.lambdaQuery().eq(HrmConfig::getType, ConfigType.ELIMINATION_REASONS.getValue()).list()
                .stream().map(HrmConfig::getValue).collect(Collectors.toList());
        eliminateVO.setRecruit(list);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, String> keyMap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                if (StrUtil.isNotBlank(list.get(i))) {
                    keyMap.put(StrUtil.toString(i), "hrm.config.1." + list.get(i));
                }
            }
            eliminateVO.setLanguageKeyMap(keyMap);
        }
        return Result.ok(eliminateVO);
    }

}

