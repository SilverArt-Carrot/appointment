package com.carrot.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.common.utils.MD5;
import com.carrot.yygh.hosp.service.HospitalSetService;
import com.carrot.yygh.model.hosp.HospitalSet;
import com.carrot.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/HospitalSet")
@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("获取所有医院信息")
    @GetMapping("/findAll")
    public Result findAll() {
        return Result.ok(hospitalSetService.list());
    }

    @ApiOperation("根据具体ID获取医院信息")
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Long id) {
        return Result.ok(hospitalSetService.getById(id));
    }

    @ApiOperation("根据分页信息和查询条件获取医院信息")
    @PutMapping("/findPage/{current}/{size}")
    public Result findPage(@PathVariable("current") Integer current, @PathVariable("size") Integer size,
                           @RequestBody(required = false) HospitalSetQueryVo vo) {
        Page<HospitalSet> page = new Page<>(current, size);

        QueryWrapper<HospitalSet> wrapper = null;
        if (vo != null) {
            wrapper = new QueryWrapper<>();
            String name = vo.getHosname();
            String code = vo.getHoscode();
            if (!StringUtils.isEmpty(name)) {
                wrapper.like("hosname", name);
            }
            if (!StringUtils.isEmpty(code)) {
                wrapper.eq("hoscode", code);
            }
            return Result.ok(hospitalSetService.page(page, wrapper));
        }

        return Result.ok(hospitalSetService.page(page));
    }

    @ApiOperation("删除医院")
    @DeleteMapping("/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        return hospitalSetService.removeById(id) ? Result.ok() : Result.fail();
    }

    @ApiOperation("批量删除医院信息")
    @PutMapping("/removeBatch")
    public Result removeBatch(@RequestBody List<Long> ids) {
        return hospitalSetService.removeByIds(ids) ? Result.ok() : Result.fail();
    }

    @ApiOperation("保存医院信息")
    @PostMapping("/save")
    public Result save(@RequestBody HospitalSet hospitalSet) {
        //设置医院状态，默认可用
        hospitalSet.setStatus(1);
        //设置医院签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        return hospitalSetService.save(hospitalSet) ? Result.ok() : Result.fail();
    }

    @ApiOperation("修改医院信息")
    @PutMapping("/update")
    public Result update(@RequestBody HospitalSet hospitalSet) {
        return hospitalSetService.updateById(hospitalSet) ? Result.ok() : Result.fail();
    }

    @ApiOperation("更新医院状态")
    @PutMapping("/status/{id}/{status}")
    public Result status(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        UpdateWrapper<HospitalSet> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set("status", status);
        return hospitalSetService.update(wrapper) ? Result.ok() : Result.fail();
    }

    @ApiOperation("发送签名密钥")
    @GetMapping("sendKey/{id}")
    public Result sendKey(@PathVariable("id") Long id) {
        HospitalSet hospital = hospitalSetService.getById(id);
        String code = hospital.getHoscode();
        String key = hospital.getSignKey();

        //TODO 发送短信

        return Result.ok();
    }
}
