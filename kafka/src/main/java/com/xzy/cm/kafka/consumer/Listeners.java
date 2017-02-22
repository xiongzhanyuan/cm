package com.xzy.cm.kafka.consumer;

/**
 * Created by xiongzhanyuan on 2017/2/14.
 */
public class Listeners {

    //fixme 在引用cm包的工程里 编写KafkaListeners类 写法如下

//    @KafkaListener(topics = {"testTopic"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            System.out.println(message);
//        }
//    }
}
