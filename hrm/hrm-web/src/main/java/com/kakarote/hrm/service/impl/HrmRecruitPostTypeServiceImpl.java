package com.kakarote.hrm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.RecursionUtil;
import com.kakarote.hrm.common.LanguageFieldUtil;
import com.kakarote.hrm.entity.PO.HrmRecruitPostType;
import com.kakarote.hrm.mapper.HrmRecruitPostTypeMapper;
import com.kakarote.hrm.service.IHrmRecruitPostTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 职位类型 服务实现类
 * </p>
 *
 * @author huangmingbo
 * @since 2020-05-12
 */
@Service
public class HrmRecruitPostTypeServiceImpl extends BaseServiceImpl<HrmRecruitPostTypeMapper, HrmRecruitPostType> implements IHrmRecruitPostTypeService {

    @Override
    public List<HrmRecruitPostType> queryPostType() {
        List<HrmRecruitPostType> list = list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HrmRecruitPostType postType : list) {
                //设置多语言key
                postType.setLanguageKeyMap(LanguageFieldUtil.getFieldNameKeyMap("name_resourceKey", "hrm.postType.", postType.getName()));
            }
        }
        List<HrmRecruitPostType> childListTree = RecursionUtil.getChildListTree(list, "parentId", 0L, "id", "children", HrmRecruitPostType.class);
        return childListTree;
    }
}
