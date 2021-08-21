package com.carrot.yygh.hosp.service;

import com.carrot.yygh.model.hosp.Schedule;
import com.carrot.yygh.vo.hosp.ScheduleOrderVo;
import com.carrot.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getScheduleRole(Integer page, Integer limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获取可预约的排班数据
    Map<String,Object> getBookingScheduleRule(int page,int limit,String hoscode,String depcode);

    //获取排班id获取排班数据
    Schedule getScheduleId(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据 用于mp
    void update(Schedule schedule);
}
