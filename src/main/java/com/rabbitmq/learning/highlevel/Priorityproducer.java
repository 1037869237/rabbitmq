package com.rabbitmq.learning.highlevel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.util.HashMap;
import java.util.Map;

public class Priorityproducer {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(RabbitMQ.PORT);
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("priorityexchange","direct",true);
        /**
         * 优先级队列：
         * 具有高优先级的队列具有高的优先权，优先级高的消息具备优先被消费的特权
         */
        Map<String,Object> map = new HashMap<>();
        map.put("x-max-priority",10);                    //设置队列的最大优先权
        channel.queueDeclare("queue.priority",true,false,false,map);   //声明一个队列，最大优先权是10的队列
        channel.queueBind("queue.priority","priorityexchange","rk");

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.priority(5);           //声明了这个一个消息的优先权是5的消息
        AMQP.BasicProperties properties = builder.build();

        channel.basicPublish("priorityexchange","rk",properties,"prioruty".getBytes());
        channel.close();
        connection.close();

        /**
         * 默认最低是0，最高为队列设置的最大优先级
         * 优先级高的消息可以优先被消费。
         */
    }
}
