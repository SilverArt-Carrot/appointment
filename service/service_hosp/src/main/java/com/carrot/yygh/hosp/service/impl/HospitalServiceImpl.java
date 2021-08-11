package com.carrot.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.carrot.cmn.client.DictFeignClient;
import com.carrot.yygh.hosp.repository.HospitalRepository;
import com.carrot.yygh.hosp.service.HospitalService;
import com.carrot.yygh.model.hosp.Hospital;
import com.carrot.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;  // 注入远程调用服务

    @Override
    public void save(Map<String, Object> paramMap) {
        // 将参数map转换为对象 Hospital
        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        // 判断是否存在相同的数据
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());

        //如果存在，进行修改
        if (hospitalExist != null) {
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
        } else {//如果不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
        }
        hospital.setUpdateTime(new Date());
        hospital.setIsDeleted(0);
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);
        // 创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        // 将vo转换为hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 创建Example对象
        Example<Hospital> example = Example.of(hospital, matcher);

        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        // 对医院进行类型设置
        pages.getContent().stream().forEach(this::setHospitalHosType);
        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        // 先根据id查询
        Hospital hospital = hospitalRepository.findById(id).get();

        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getById(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospitalWithInfo = this.setHospitalHosType(hospitalRepository.findById(id).get());

        result.put("hospital", hospitalWithInfo);
        result.put("bookingRule", hospitalWithInfo.getBookingRule());
        // 不需要重复返回
        hospitalWithInfo.setBookingRule(null);

        return result;
    }

    @Override
    public String getHospNameByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode).getHosname();
    }

    public Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());

        //查询省 市  地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }
}
