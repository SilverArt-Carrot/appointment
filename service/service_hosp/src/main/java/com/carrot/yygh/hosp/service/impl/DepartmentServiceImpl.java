package com.carrot.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.carrot.yygh.hosp.repository.DepartmentRepository;
import com.carrot.yygh.hosp.service.DepartmentService;
import com.carrot.yygh.model.hosp.Department;
import com.carrot.yygh.vo.hosp.DepartmentQueryVo;
import com.carrot.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    // 添加科室信息
    @Override
    public void save(Map<String, Object> paramMap) {
        String string = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(string, Department.class);

        // 根据医院编号和科室编号进行查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        // 存在则更新
        if (departmentExist != null) {
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        } else { // 不存在则添加
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        // 创建Pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);

        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);

        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    // 根据医院编号查询科室列表
    @Override
    public List<DepartmentVo> getDeptTree(String hoscode) {
        // 最终封装的数据
        List<DepartmentVo> result = new ArrayList<>();

        // 根据医院编号，查询所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        // 查询所有信息列表
        List<Department> departmentList = departmentRepository.findAll(example);

        // 根据大科室编号对原始数据进行分组
        Map<String, List<Department>> collect = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        // 对collect进行遍历
        for (Map.Entry<String, List<Department>> entry: collect.entrySet()) {
            // 创建大科室对象
            DepartmentVo vo = new DepartmentVo();
            // 设置大科室编号
            vo.setDepcode(entry.getKey());
            // 设置大科室名称
            vo.setDepname(entry.getValue().get(0).getBigname());

            // 创建大科室下的子科室列表
            List<DepartmentVo> children = new ArrayList<>();
            // 对entry中的department列表进行遍历
            for (Department department: entry.getValue()) {
                // 创建子科室列表中的对象
                DepartmentVo voOfChild = new DepartmentVo();
                // 设置子科室编号
                voOfChild.setDepcode(department.getDepcode());
                // 设置子科室名称
                voOfChild.setDepname(department.getDepname());
                // 添加到子科室列表中
                children.add(voOfChild);
            }
            // 设置大科室的子科室列表
            vo.setChildren(children);
            // 将大科室添加到最后要返回的科室树中
            result.add(vo);
        }
        return result;
    }

    @Override
    public String getDepNameByHoscodeAndDepcode(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return department.getDepname();
    }
}
