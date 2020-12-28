package com.rabbitmq.learning.base;

import com.rabbitmq.client.*;
import com.rabbitmq.learning.entity.RabbitMQ;

public class PullConsumer {
    public static void main(String[] args) throws Exception{
        Address[] addresses = new Address[]{
                new Address(RabbitMQ.IP_ADDR,RabbitMQ.PORT)
        };
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(RabbitMQ.USER);
        connectionFactory.setPassword(RabbitMQ.PASSWORD);

        Connection connection = connectionFactory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        /**
         * 拉模式：
         *      basicGet(String queue,boolean autoAck);
         * queue:队列名称
         * autoAck：若为false，同样需要basicAck来确认消息已被成功接收
         */
        /**
         * 消息确认：
         * channel.basicAck 当消费者订阅队列的时候，可以显示的采用手动ack，如果不采用，则只要一发出就会把数据从内存或者硬盘中干掉
         * mq如果迟迟没收到ack标示，它会一直等待，直到这个客户端与服务器断开连接，才会重新入队，否则mq消费一条消息好久好久
         *
         * 消息拒绝：
         * channel.basicReject(Long deliveryTag,boolean requeue)
         */
        GetResponse response = channel.basicGet("secondqueue", false);

        System.out.println(new String(response.getBody()));
        channel.basicAck(response.getEnvelope().getDeliveryTag(),false);

        System.out.println(response.getProps().getDeliveryMode());
        System.out.println("-------------");
       // channel.close();
        connection.close();
    }
}
