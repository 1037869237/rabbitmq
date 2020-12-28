package com.rabbitmq.learning.base;

import com.rabbitmq.client.*;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.io.IOException;

public class PushConsumer {
    public static void main(String[] args) throws  Exception{
        Address[] addresses = new Address[]{
                new Address(RabbitMQ.IP_ADDR,RabbitMQ.PORT)
        };
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(RabbitMQ.USER);
        connectionFactory.setPassword(RabbitMQ.PASSWORD);

        Connection connection = connectionFactory.newConnection(addresses);
        final Channel channel = connection.createChannel();

        /**
         * 1.  推送消息模式
         *
         * queue:队列
         * autoAck：是否自动确认，建议设置为false，不自动确认
         * consumerTag:消费者标签，用来区分多个消费者，不同的订阅采用不同的消费者标签来区分彼此，在同一个channel中的消费者也需要通过唯一的消费者标签来区分
         *  noLocal:为true：表示不能将同一个connection中的生产者发送的消息传送给这个connection中的消费者
         *  exclusive：消费者是否排他
         *  argument：设置消费者的其他参数
         *  callback：设置消费者的回调函数。用来处理mq推送过来的消息，比如defaultconsumer，使用时需要客户端重写其中的方法，这个可以重写很多的方法，最简单的是重写handleDelivery
         *
         * channel.basicConsume(String queue,Consumer callback)
         * channel.basicConsume(String queue,boolean autoAck,String consumerTag,Consumer callback)
         * Consumer consumer = new DefaultConsumer(channel){
         *             @Override
         *             public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
         *                 System.out.println("rec message"+new String(body));
         *                 try{
         *                     Thread.sleep(1000);
         *                 }catch (Exception e){
         *                     e.printStackTrace();
         *                 }
         *                 channel.basicAck(envelope.getDeliveryTag(),false);
         *             }
         * }
         *
         * 其他可以重写的方法：
         * 1。 handleConsumeOk：方法会在其他方法之前调用，返回消费者标签
         * 2。 handleShutdownSignal:当channel或者connection关闭的时候会调用
         * 3。 handleCancleOk:显示的取消订阅的时候调用，channel.basicCancel方法显示的取消一个消费者订阅
         */
        channel.basicQos(64);
        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("rec message"+new String(body));
                System.out.println(properties.getContentType());
                System.out.println(properties.getDeliveryMode());
                System.out.println(consumerTag);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(),false);
            }

            public void handleConsumeOk(String consumerTag){
                System.out.println(consumerTag);
            }
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

                System.out.println("关闭ing");
            }
        };
        channel.basicConsume("secondqueue",false,"consumerTag",consumer);
        //等待回调函数完事
        Thread.sleep(3000);
        System.out.println("----------------");
        channel.close();
        connection.close();
    }
}
