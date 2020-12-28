package com.rabbitmq.learning.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.learning.entity.RabbitMQ;

/**
 * 熟悉mq的工作流程，并且他的底层的工作模式清晰
 */
public class MessageProducer {
    public static void main(String[] args) throws  Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RabbitMQ.IP_ADDR);
        connectionFactory.setPort(RabbitMQ.PORT);
        connectionFactory.setUsername(RabbitMQ.USER);
        connectionFactory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //创建一个类型为direct的交换机，持久化的，非自动删除的容器
        channel.exchangeDeclare("firstexchange","direct",true,false,null);
        //创建一个持久化的，非排他的，非自动删除的队列
        channel.queueDeclare("firstqueue",true,false,false,null);
        //将交换机与队列通过路由键进行绑定
        channel.queueBind("firstqueue","firstexchange","firstrouterkey");
        //发送一条持久化消息
        String message = "hello world";
        channel.basicPublish("firstexchange","firstrouterkey", MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        //关闭资源
        channel.close();
        connection.close();
    }
}
