package Processor.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "Actions", groupId = "Actions")
    public void listen(String message) {
        //TODO Pull data from actions topic
    }
}
