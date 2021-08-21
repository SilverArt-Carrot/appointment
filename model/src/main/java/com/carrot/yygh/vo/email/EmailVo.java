package com.carrot.yygh.vo.email;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "邮件实体")
public class EmailVo {

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "邮件模板code")
    private String templateCode;

    @ApiModelProperty(value = "邮件模板参数")
    private Map<String,Object> param;
}
