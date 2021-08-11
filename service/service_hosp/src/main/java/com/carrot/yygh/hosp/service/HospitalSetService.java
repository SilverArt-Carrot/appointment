package com.carrot.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carrot.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKeyByHoscode(String hoscode);
}
