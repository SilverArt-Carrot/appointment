package com.carrot.yygh.hosp.service;

import com.carrot.yygh.model.hosp.Department;
import com.carrot.yygh.vo.hosp.DepartmentQueryVo;
import com.carrot.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> getDeptTree(String hoscode);

    String getDepNameByHoscodeAndDepcode(String hoscode, String depcode);
}
