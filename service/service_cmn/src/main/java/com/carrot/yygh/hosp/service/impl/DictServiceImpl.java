package com.carrot.yygh.hosp.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carrot.yygh.hosp.listener.DictListener;
import com.carrot.yygh.hosp.mapper.DictMapper;
import com.carrot.yygh.hosp.service.DictService;
import com.carrot.yygh.model.cmn.Dict;
import com.carrot.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
        implements DictService {

    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        for (Dict dict: dictList) {
            dict.setHasChildren(hasChildren(dict.getId()));
        }
        return dictList;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        //查询数据库数据字典全部信息
        List<Dict> dictList = baseMapper.selectList(null);

        //将Dict转化为DictEeVo
        ArrayList<DictEeVo> dictEeVoList = new ArrayList<>();
        for (Dict dict: dictList) {
            DictEeVo eeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, eeVo);
            dictEeVoList.add(eeVo);
        }

        // 调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importData(MultipartFile exel) {
        //导入数据字典
        try {
            EasyExcel.read(exel.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName(String dictCode, String value) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();

        if (StringUtils.isEmpty(dictCode)) {
            wrapper.eq("value", value);
        } else {
            Long parentId = getDictByDictCode(dictCode).getId();
            wrapper.eq("value", value)
                    .eq("parent_id", parentId);
        }
        return baseMapper.selectOne(wrapper).getName();
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Long parentId = getDictByDictCode(dictCode).getId();

        return this.findChildData(parentId);
    }

    //判断是否还有子节点
    public boolean hasChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        return baseMapper.selectCount(wrapper) > 0;
    }

    // 根据dict_code查询
    public Dict getDictByDictCode(String dictCode) {
        return baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
    }
}
