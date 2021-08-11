package com.carrot.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carrot.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile exel);

    String getName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
