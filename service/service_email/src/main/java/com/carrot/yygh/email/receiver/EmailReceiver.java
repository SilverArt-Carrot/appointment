package com.carrot.yygh.email.receiver;

import com.rabbitmq.client.Channel;
import com.carrot.yygh.email.service.EmailService;
import com.carrot.yygh.util.rabbitmq.constant.MQConst;
import com.carrot.yygh.vo.email.EmailVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailReceiver {

    @Autowired
    private EmailService emailService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_MSM),
            key = {MQConst.ROUTING_MSM_ITEM}
    ))
    public void send(EmailVo emailVo, Message message, Channel channel) {
        emailService.send(emailVo);
    }
}
