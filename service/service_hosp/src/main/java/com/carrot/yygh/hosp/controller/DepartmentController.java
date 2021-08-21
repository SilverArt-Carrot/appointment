package com.carrot.yygh.hosp.controller;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.hosp.service.DepartmentService;
import com.carrot.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin
@Api(tags = "科室信息")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("根据医院编号查询科室列表")
    @GetMapping("/getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable("hoscode") String hoscode) {
        List<DepartmentVo> list = departmentService.getDeptTree(hoscode);
        return Result.ok(list);
    }
}
