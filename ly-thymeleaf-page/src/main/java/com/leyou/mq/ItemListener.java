package com.leyou.mq;

import com.leyou.service.StaticPageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/16
 * @Description:   监听rabbitmq发送过来的信息
 * @version: 1.0
 */
@Component
public class ItemListener {

    @Autowired
    private StaticPageService staticPageService;
    /**
     * 处理insert和update的消息
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "staticPage.item.insertOrUpdate.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key ={"item.insert","item.update"}
    ))
    public void listenerInsertOrUpdate(Long spuId){
        if(spuId == null){
            return;
        }
        //处理MQ消息，生成新的html
        staticPageService.createOrUpdateHtml(spuId);

    }

    /**
     * 处理delete的消息
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "staticPage.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key ={"item.delete"}
    ))
    public void listenerDelete(Long spuId){
        if(spuId == null){
            return;
        }
        //处理MQ消息，根据spuid删除静态页
        staticPageService.deleteHtmlById(spuId);

    }
}
