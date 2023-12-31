package com.kakarote.hrm.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.core.common.Result;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.entity.PO.HrmRecruitChannel;
import com.kakarote.hrm.service.IHrmRecruitChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 招聘渠道表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-02
 */
@RestController
@RequestMapping("/hrmRecruitChannel")
@Api(tags = "招聘渠道-表单使用")
public class HrmRecruitChannelController {

    @Autowired
    private IHrmRecruitChannelService recruitChannelService;


    @PostMapping("/queryRecruitChannelList")
    @ApiOperation("查询招聘渠道列表")
    public Result<List<HrmRecruitChannel>> queryRecruitChannelList() {
        List<HrmRecruitChannel> list = recruitChannelService.lambdaQuery()
                .eq(HrmRecruitChannel::getStatus, 1).list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HrmRecruitChannel channel : list) {
                //添加语言包key
                channel.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("value_resourceKey", "admin.recruitChannel.", channel.getValue()));
            }
        }
        return Result.ok(list);
    }
}

