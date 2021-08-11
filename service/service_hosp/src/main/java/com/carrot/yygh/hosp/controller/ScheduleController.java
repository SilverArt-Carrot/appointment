package com.carrot.yygh.hosp.controller;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.hosp.service.ScheduleService;
import com.carrot.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "排班管理")
@RequestMapping("/admin/hosp/schedule")
@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("查询排班规则数据")
    @GetMapping("/getScheduleRole/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRole(@PathVariable("page") Integer page,
                                  @PathVariable("limit") Integer limit,
                                  @PathVariable("hoscode") String hoscode,
                                  @PathVariable("depcode") String depcode) {

        Map<String, Object> result = scheduleService.getScheduleRole(page, limit, hoscode, depcode);
        return Result.ok(result);
    }

    // 根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail( @PathVariable("hoscode") String hoscode,
                                     @PathVariable("depcode") String depcode,
                                     @PathVariable("workDate") String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(list);
    }
}
