package com.carrot.yygh.email.controller;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.email.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "邮件验证码接口")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @ApiOperation("发送邮件验证码")
    @GetMapping("/send/{email}")
    public Result send(@PathVariable("email") String email) {
        return emailService.send(email);
    }
}
