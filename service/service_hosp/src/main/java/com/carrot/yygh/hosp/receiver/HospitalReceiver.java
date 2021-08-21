package com.carrot.yygh.hosp.receiver;

import com.carrot.yygh.hosp.service.ScheduleService;
import com.carrot.yygh.model.hosp.Schedule;
import com.carrot.yygh.util.rabbitmq.constant.MQConst;
import com.carrot.yygh.util.rabbitmq.service.RabbitService;
import com.carrot.yygh.vo.email.EmailVo;
import com.carrot.yygh.vo.msm.MsmVo;
import com.carrot.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_ORDER),
            key = {MQConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
        if(null != orderMqVo.getAvailableNumber()) {
            //下单成功更新预约数
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
        } else {
            //取消预约更新预约数
            int availableNumber = schedule.getAvailableNumber().intValue() + 1;
            schedule.setAvailableNumber(availableNumber);
        }
        scheduleService.update(schedule);

        //发送邮件
        EmailVo emailVo = orderMqVo.getEmailVo();
        if(null != emailVo) {
            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_MSM, MQConst.ROUTING_MSM_ITEM, emailVo);
        }
    }

}
