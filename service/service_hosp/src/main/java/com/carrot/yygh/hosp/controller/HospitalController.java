package com.carrot.yygh.hosp.controller;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.hosp.service.HospitalService;
import com.carrot.yygh.model.hosp.Hospital;
import com.carrot.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医院管理")
@RestController
@RequestMapping("/admin/hosp/Hospital")
//@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;  // mongoDB

    // 医院列表（添加查询分页）
    @ApiOperation("获取医院列表")
    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable("page") Integer page, @PathVariable("limit") Integer limit,
                           HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);

        return Result.ok(pageModel);
    }

    @ApiOperation("更新医院上线状态")
    @PutMapping("/updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable("id") String id, @PathVariable("status") Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    @ApiOperation("根据医院id查询详情信息")
    @GetMapping("/getHospById/{id}")
    public Result getHospById(@PathVariable("id") String id) {
        return Result.ok(hospitalService.getById(id));
    }
}
