package com.carrot.yygh.email.service.impl;

import com.carrot.yygh.common.result.Result;
import com.carrot.yygh.email.service.EmailService;
import com.carrot.yygh.email.utils.RandomUtils;
import com.carrot.yygh.vo.email.EmailVo;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result send(String email) {
        // 如果已经发送，则直接返回
        String code = redisTemplate.opsForValue().get(email);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }

        // 生成验证码，
        code = RandomUtils.getSixBitRandom();
        //调用发送邮件方法
        boolean isSend = this.send(email, code);
        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            // 发送成功后，5分钟后过期
            redisTemplate.opsForValue().set(email, code,5, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }

    // 由rabbitMQ调用
    @Override
    public boolean send(EmailVo emailVo) {
        if(!StringUtils.isEmpty(emailVo.getEmail())) {
            return this.send(emailVo.getEmail(), emailVo.getParam());
        }
        return false;
    }

    // 邮件通知预约信息
    private boolean send(String email, Map<String,Object> param){
        String name = (String)param.get("name");   // 就诊人名称
        String title = (String)param.get("title");   // 邮件标题
        Integer amount = (Integer) param.get("amount");  // 服务费
        String reserveDate = (String) param.get("reserveDate");  // 预约时间

        // 发送预约成功短信
        try {
            HtmlEmail e = new HtmlEmail();
            e.setHostName("smtp.qq.com");
            e.setCharset("UTF-8");
            e.addTo(email);// 收件地址

            e.setFrom("2080530488@qq.com", "统一预约挂号平台");

            e.setAuthentication("2080530488@qq.com", "");

            e.setSubject("统一预约挂号平台"); // 此处填写邮件名
            e.setMsg("尊敬的" + name + "，恭喜您预约成功，您本次的就诊时间为：" +
                    reserveDate + "，请在指定时间到" + title + "处就诊，本次服务费为：" +
                    amount.toString() + "元"); // 此处填写邮件内容

            e.send();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // 发送邮箱验证码方法
    private boolean send(String email, String code) {
        try {
            HtmlEmail e = new HtmlEmail();
            e.setHostName("smtp.qq.com");
            e.setCharset("UTF-8");
            e.addTo(email);// 收件地址

            e.setFrom("2080530488@qq.com", "统一预约挂号平台");

            e.setAuthentication("2080530488@qq.com", "hldtrozryhgceche");

            e.setSubject("统一预约挂号平台");//此处填写邮件名，邮件名可任意填写
            e.setMsg("尊敬的用户您好,您本次的验证码是:" + code + ", 5分钟后过期"); //此处填写邮件内容

            e.send();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
