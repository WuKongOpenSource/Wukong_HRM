package com.kakarote.hrm.controller;

import com.kakarote.core.common.Result;
import com.kakarote.core.entity.BasePage;
import com.kakarote.core.entity.PageEntity;
import com.kakarote.hrm.entity.PO.HrmAttendanceGroupRelationEmployee;
import com.kakarote.hrm.service.IHrmAttendanceGroupRelationEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 考勤组关联员工表 前端控制器
 * </p>
 *
 * @author guomenghao
 * @since 2021-11-26
 */
@RestController
@RequestMapping("/hrmAttendanceGroupRelationEmployee")
@Api(tags = "考勤管理-考勤组关联员工")
public class HrmAttendanceGroupRelationEmployeeController {

    @Autowired
    private IHrmAttendanceGroupRelationEmployeeService hrmAttendanceGroupRelationEmployeeService;

    @PostMapping("/queryById/{id}")
    @ApiOperation("根据ID查询")
    public Result<HrmAttendanceGroupRelationEmployee> queryById(@PathVariable("id") @ApiParam(name = "id", value = "id") Serializable id) {
        HrmAttendanceGroupRelationEmployee entity = hrmAttendanceGroupRelationEmployeeService.queryById(id);
        return Result.ok(entity);
    }

    @PostMapping("/add")
    @ApiOperation("保存数据")
    public Result add(@RequestBody HrmAttendanceGroupRelationEmployee entity) {
        hrmAttendanceGroupRelationEmployeeService.addOrUpdate(entity);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("修改数据")
    public Result update(@RequestBody HrmAttendanceGroupRelationEmployee entity) {
        hrmAttendanceGroupRelationEmployeeService.addOrUpdate(entity);
        return Result.ok();
    }

    @PostMapping("/queryPageList")
    @ApiOperation("查询列表页数据")
    public Result<BasePage<HrmAttendanceGroupRelationEmployee>> queryPageList(@RequestBody PageEntity search) {
        search.setPageType(1);
        return Result.ok(hrmAttendanceGroupRelationEmployeeService.queryPageList(search));
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("根据ID删除数据")
    public Result deleteByIds(@ApiParam(name = "ids", value = "id列表") @RequestBody List<Serializable> ids) {
        hrmAttendanceGroupRelationEmployeeService.deleteByIds(ids);
        return Result.ok();
    }
}
