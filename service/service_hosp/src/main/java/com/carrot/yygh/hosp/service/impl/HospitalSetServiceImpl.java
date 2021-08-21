package com.carrot.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carrot.yygh.common.exception.AppointmentException;
import com.carrot.yygh.common.result.ResultCodeEnum;
import com.carrot.yygh.hosp.mapper.HospitalSetMapper;
import com.carrot.yygh.hosp.service.HospitalSetService;
import com.carrot.yygh.model.hosp.HospitalSet;
import com.carrot.yygh.vo.order.SignInfoVo;
import org.springframework.stereotype.Service;


@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService
{

    // 根据hoscode查询数据
    @Override
    public String getSignKeyByHoscode(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }

    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(null == hospitalSet) {
            throw new AppointmentException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }
}
