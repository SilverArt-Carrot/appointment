package com.carrot.yygh.hosp.service;

import com.carrot.yygh.model.hosp.Hospital;
import com.carrot.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> resultMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getById(String id);

    String getHospNameByHoscode(String hoscode);
}
