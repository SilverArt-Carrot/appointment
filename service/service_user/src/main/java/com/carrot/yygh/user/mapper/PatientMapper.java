package com.carrot.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carrot.yygh.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
