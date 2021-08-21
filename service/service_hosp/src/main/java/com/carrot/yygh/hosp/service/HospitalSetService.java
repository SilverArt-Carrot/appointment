package com.carrot.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carrot.yygh.model.hosp.HospitalSet;
import com.carrot.yygh.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKeyByHoscode(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
