package com.carrot.yygh.hosp.client;

import com.carrot.yygh.vo.hosp.ScheduleOrderVo;
import com.carrot.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hosp")
@Repository
public interface HospFeignClient {

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(@ApiParam(name = "scheduleId", value = "排班id", required = true)
                                                  @PathVariable("scheduleId") String scheduleId);

    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);
}
