package com.rabbitmq.learning.highlevel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.util.HashMap;
import java.util.Map;

public class DLXproducer {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(RabbitMQ.PORT);
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * 当一个消息的ttl过期了，那么这条消息则变成了死信，如果在一个队列中变成了死信的话，那么它可以被转发给另外一个交换机，它就是DLX死信交换机，与它绑定的队列是死信队列
         * 消息变成死信的情况：
         * 1。 消息被拒绝
         * 2。 消息过期
         * 3。 队列达到最大长度
         * DLX也是一个正常的交换机，和正常的没有区别。
         */

        /**
         * 下面创建一个ttl和dlx来模拟延迟队列的效果
         */
        channel.exchangeDeclare("exchange.dlx","direct",true);   //创建一个死心交换机
        channel.exchangeDeclare("exchange.normal","fanout",true);  //创建一个正常交换机

        Map<String,Object> map = new HashMap<>();    //为正常的队列设置一些参数
        map.put("x-message-ttl",10000);              //队列的ttl
        map.put("x-dead-letter-exchange","exchange.dlx");        //死信之后，队列转发给哪个死信交换机
        map.put("x-dead-letter-routing-key","routingkey");       //转发死信的路由键是什么

        channel.queueDeclare("queue.normal",true,false,false,map);    //声明正常的队列
        channel.queueBind("queue.normal","exchange.normal","");          //与正常的交换机进行绑定

        channel.queueDeclare("queue.dlx",true,false,false,null);    //声明死信队列
        channel.queueBind("queue.dlx","exchange.dlx","routingkey");          //死信队列和死信交换机进行绑定

        channel.basicPublish("exchange.normal","routingkey", MessageProperties.PERSISTENT_TEXT_PLAIN,"dlx test".getBytes());   //发送消息

        channel.close();
        connection.close();
        /**
         * 根据ttl+dsl可以做延迟队列，
         */

    }
}
