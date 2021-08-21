package com.carrot.yygh.hosp.controller.api;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.hosp.service.DepartmentService;
import com.carrot.yygh.hosp.service.HospitalService;
import com.carrot.yygh.hosp.service.HospitalSetService;
import com.carrot.yygh.hosp.service.ScheduleService;
import com.carrot.yygh.model.hosp.Hospital;
import com.carrot.yygh.model.hosp.Schedule;
import com.carrot.yygh.vo.hosp.HospitalQueryVo;
import com.carrot.yygh.vo.hosp.ScheduleOrderVo;
import com.carrot.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "用户界面接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class UserSiteApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("获取医院分页列表")
    @GetMapping("/list/{page}/{limit}")
    public Result list(@PathVariable("page") Integer page, @PathVariable("limit") Integer limit,
                       HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    @ApiOperation("获取医院名称查询")
    @GetMapping("/findByHosname/{hosname}")
    public Result findByName(@PathVariable("hosname") String hosname) {
        List<Hospital> hospitalList = hospitalService.getListByHosname(hosname);
        return Result.ok(hospitalList);
    }

    @ApiOperation(value = "获取科室列表")
    @GetMapping("/department/{hoscode}")
    public Result index(@PathVariable("hoscode") String hoscode) {
        return Result.ok(departmentService.getDeptTree(hoscode));
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("/findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode) {
        Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

    @ApiOperation(value = "获取排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable String scheduleId) {
        Schedule schedule = scheduleService.getScheduleId(scheduleId);
        return Result.ok(schedule);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    @ApiOperation(value = "获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }
}
