package com.rabbitmq.learning.highlevel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.util.HashMap;
import java.util.Map;

public class TTLProducer {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(RabbitMQ.PORT);
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * 设置消息的TTL有两种方法：
         * 1。 第一种是通过队列属性设置，队列中所有消息都有相同的过期时间。
         * 2。 第二种方法是对消息本身进行单独设置，每条消息的TTL可以不同
         *
         * 如果两种方法一起使用，则消息的TTL以两者较小的为准。
         * 如果生存时间一旦超过设置的TT，就会变成死信。
         */
        Map<String,Object> map = new HashMap<>();  //设置ttl参数，第一种方法，在声明队列的时候带上ttl参数
        map.put("x-message-ttl",6000);
        channel.queueDeclare("ttlqueue",false,false,false,map);

        channel.exchangeDeclare("ttlexchange","direct",true,false,null);
        channel.queueBind("ttlqueue","ttlexchange","ttlkey");

        /**
         * 第二种方式，在发送消息的时候设置参数
         */
        AMQP.BasicProperties.Builder builder= new AMQP.BasicProperties.Builder();
        builder.expiration("60000");
        AMQP.BasicProperties properties = builder.build();
        channel.basicPublish("ttlexchange","ttlkey",properties,"ttl test".getBytes());
        channel.close();
        connection.close();
        /**
         * 总结：
         * 针对队列设置ttl属性方法，一旦消息过期，就会从队列中抹去，因为这种方法队列中已过期的消息肯定在队列头部，定期的从头开始扫描即可
         * 针对消息设置ttl方法，即使消息过期也不会立马删除，得等这条消息到达队列的头部，才会删除。
         */
    }
}
