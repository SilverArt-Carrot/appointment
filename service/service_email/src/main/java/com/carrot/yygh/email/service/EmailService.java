package com.carrot.yygh.email.service;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.vo.email.EmailVo;

public interface EmailService {
    Result send(String email);

    boolean send(EmailVo emailVo);
}
