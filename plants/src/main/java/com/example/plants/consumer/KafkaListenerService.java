package com.example.plants.consumer;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    //Log log_kafka = new Log();
    Logger log_kafka = LogManager.getLogger(KafkaListenerService.class);

    @KafkaListener(topics = "plants.demo", groupId = "consumer-plant-demo")
    public void consume(@Payload String message,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        log_kafka.info("Received Message: " + message
                        + "from partition: " + partition);
    }
}
