package com.kakarote.hrm.controller;

import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.PO.HrmSalaryGroupRelationDept;
import com.kakarote.hrm.service.IHrmSalaryGroupRelationDeptService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 薪资组关联部门表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-11-26
 */
@RestController
@RequestMapping("/hrmSalaryGroupRelationDept")
public class HrmSalaryGroupRelationDeptController {

    @Autowired
    private IHrmSalaryGroupRelationDeptService hrmSalaryGroupRelationDeptService;

    @PostMapping("/queryById/{id}")
    @ApiOperation("根据ID查询")
    public Result<HrmSalaryGroupRelationDept> queryById(@PathVariable("id") @ApiParam(name = "id", value = "id") Serializable id) {
        HrmSalaryGroupRelationDept entity = hrmSalaryGroupRelationDeptService.queryById(id);
        return Result.ok(entity);
    }

    @PostMapping("/add")
    @ApiOperation("保存数据")
    public Result add(@RequestBody HrmSalaryGroupRelationDept entity) {
        hrmSalaryGroupRelationDeptService.addOrUpdate(entity);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("修改数据")
    public Result update(@RequestBody HrmSalaryGroupRelationDept entity) {
        hrmSalaryGroupRelationDeptService.addOrUpdate(entity);
        return Result.ok();
    }

    @PostMapping("/queryPageList")
    @ApiOperation("查询列表页数据")
    public Result<BasePage<HrmSalaryGroupRelationDept>> queryPageList(@RequestBody PageEntity search) {
        search.setPageType(1);
        return Result.ok(hrmSalaryGroupRelationDeptService.queryPageList(search));
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("根据ID删除数据")
    public Result deleteByIds(@ApiParam(name = "ids", value = "id列表") @RequestBody List<Serializable> ids) {
        hrmSalaryGroupRelationDeptService.deleteByIds(ids);
        return Result.ok();
    }
}
