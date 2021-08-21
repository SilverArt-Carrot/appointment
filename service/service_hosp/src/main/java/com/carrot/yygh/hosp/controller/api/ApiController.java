package com.carrot.yygh.hosp.controller.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.carrot.yygh.common.exception.AppointmentException;
import com.carrot.yygh.common.helper.HttpRequestHelper;
import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.common.result.ResultCodeEnum;
import com.carrot.yygh.common.utils.MD5;
import com.carrot.yygh.hosp.service.DepartmentService;
import com.carrot.yygh.hosp.service.HospitalService;
import com.carrot.yygh.hosp.service.HospitalSetService;
import com.carrot.yygh.hosp.service.ScheduleService;
import com.carrot.yygh.model.hosp.Department;
import com.carrot.yygh.model.hosp.Hospital;
import com.carrot.yygh.model.hosp.Schedule;
import com.carrot.yygh.vo.hosp.DepartmentQueryVo;
import com.carrot.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "为医院提供的api")
@RestController
@RequestMapping("/api/hosp")
//@CrossOrigin
public class ApiController {

    @Autowired
    private HospitalService hospitalService;  //mongoDB

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    // 获取医院信息
    @PostMapping("/hospital/show")
    public Result getHospital(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);
        String hoscode = (String)paramMap.get("hoscode");

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), hoscode)) {
            Hospital hospital = hospitalService.getByHoscode(hoscode);
            return Result.ok(hospital);
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    // 保存或者修改医院信息
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), (String) paramMap.get("hoscode"))) {
            // logo处理
            String logData = (String)paramMap.get("logoData");
            logData = logData.replace(" ", "+");
            paramMap.put("logoData", logData);

            // 保存医院数据
            hospitalService.save(paramMap);
            return Result.ok();
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @PostMapping("/department/list")
    public Result findDepartment(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);
        String hoscode = (String) paramMap.get("hoscode");

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), hoscode)) {
            // 取出页码
            Integer page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
            // 去除页大小
            Integer limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

            DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
            departmentQueryVo.setHoscode(hoscode);

            //调用service方法
            Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
            return Result.ok(pageModel);
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);

        String hoscode = (String) paramMap.get("hoscode");
        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), hoscode)) {
            String depcode = (String)paramMap.get("depcode");
            departmentService.remove(hoscode, depcode);

            return Result.ok();
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    // 保存科室信息
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), (String) paramMap.get("hoscode"))) {

            departmentService.save(paramMap);
            return Result.ok();
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), (String) paramMap.get("hoscode"))) {

            scheduleService.save(paramMap);
            return Result.ok();
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @PostMapping("/schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);
        String hoscode = (String)paramMap.get("hoscode");

        if (isSigned((String) paramMap.get("sign"), hoscode)) {
            // 科室编号
            String depcode = (String)paramMap.get("depcode");
            // 当前页 和 每页记录数
            int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
            int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String)paramMap.get("limit"));

            ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
            scheduleQueryVo.setHoscode(hoscode);
            scheduleQueryVo.setDepcode(depcode);

            Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
            return Result.ok(pageModel);
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        // 获取医院传递过来的信息
        Map<String, Object> paramMap = getParamMap(request);
        String hoscode = (String)paramMap.get("hoscode");

        // 判断是否签名正确
        if (isSigned((String) paramMap.get("sign"), hoscode)) {
            String hosScheduleId = (String)paramMap.get("hosScheduleId");
            scheduleService.remove(hoscode, hosScheduleId);

            return Result.ok();
        } else {
            throw new AppointmentException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    // 将医院传过来的数据进行处理
    public Map<String, Object> getParamMap(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        return HttpRequestHelper.switchMap(parameterMap);
    }

    public boolean isSigned(String hospSignKey, String hoscode) {
        // 对医院传过来的签名进行验证
        // 医院传过来的签名经过了MD5加密，而数据库的签名还没有MD5加密
        // 根据医院的hoscode查询出数据库签名
        String signKey = hospitalSetService.getSignKeyByHoscode(hoscode);
        // 对签名进行加密
        String encrypt = MD5.encrypt(signKey);
        // 返回签名是否一致
        return encrypt.equals(hospSignKey);
    }
}
