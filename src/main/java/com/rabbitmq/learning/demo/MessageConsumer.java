package com.rabbitmq.learning.demo;

import com.rabbitmq.client.*;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.io.IOException;

public class MessageConsumer {
    public static void main(String[] args) throws  Exception{
        Address[] addresses = new Address[]{
                new Address(RabbitMQ.IP_ADDR,RabbitMQ.PORT)
        };
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(RabbitMQ.USER);
        connectionFactory.setPassword(RabbitMQ.PASSWORD);

        Connection connection = connectionFactory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        channel.basicQos(64);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
                System.out.println("rec message"+new String(body));
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        channel.basicConsume("firstqueue",false,"consumeTag",consumer);
        //等待回调函数完事
        Thread.sleep(3000);
        channel.close();
        connection.close();
    }
}
