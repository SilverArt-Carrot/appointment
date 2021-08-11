package com.carrot.yygh.hosp.controller;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.hosp.service.DictService;
import com.carrot.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class CmnController {

    @Autowired
    private DictService dictService;

    @ApiOperation("根据父级Id查询数据")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable("id") Long id) {
        List<Dict> childData = this.dictService.findChildData(id);
        return Result.ok(childData);
    }

    @ApiOperation("根据dictCode获取下级结点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable("dictCode") String dictCode) {
        List<Dict> dicts = dictService.findByDictCode(dictCode);
        return Result.ok(dicts);
    }

    @ApiOperation("导出数据字典数据")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @ApiOperation("导入数据字典数据")
    @PostMapping("/importData")
    public Result importData(@RequestParam(value = "file") MultipartFile exel) {
        dictService.importData(exel);
        return Result.ok();
    }

    @ApiOperation("根据条件查询数据字典名称")
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
        return dictService.getName(dictCode, value);
    }

    @ApiOperation("根据条件查询数据字典名称")
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable("value") String value) {
        return dictService.getName(null, value);
    }
}
