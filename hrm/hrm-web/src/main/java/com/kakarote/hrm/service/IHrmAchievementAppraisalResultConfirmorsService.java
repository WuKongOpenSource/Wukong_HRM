package com.kakarote.hrm.service;

import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.core.servlet.BaseService;
import com.kakarote.hrm.entity.PO.HrmAchievementAppraisalResultConfirmors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 考核结果确定人 服务类
 * </p>
 *
 * @author guomenghao
 * @since 2021-12-26
 */
public interface IHrmAchievementAppraisalResultConfirmorsService extends BaseService<HrmAchievementAppraisalResultConfirmors> {

    /**
     * 查询字段配置
     *
     * @param id 主键ID
     * @return data
     * @author guomenghao
     * @since 2021-12-26
     */
    public HrmAchievementAppraisalResultConfirmors queryById(Serializable id);

    /**
     * 保存或新增信息
     *
     * @param entity entity
     * @author guomenghao
     * @since 2021-12-26
     */
    public void addOrUpdate(HrmAchievementAppraisalResultConfirmors entity);


    /**
     * 查询所有数据
     *
     * @param search 搜索条件
     * @return list
     * @author guomenghao
     * @since 2021-12-26
     */
    public BasePage<HrmAchievementAppraisalResultConfirmors> queryPageList(PageEntity search);

    /**
     * 根据ID列表删除数据
     *
     * @param ids ids
     * @author guomenghao
     * @since 2021-12-26
     */
    public void deleteByIds(List<Serializable> ids);
}
