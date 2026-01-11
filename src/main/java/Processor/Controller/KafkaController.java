package Processor.Controller;

import Processor.Kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
class KafkaController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/sendEmail")
    public String sendMessage(@RequestParam String message) {
        kafkaProducer.sendMessage("EmailTopic",message);
        System.out.println("Message sent to topic");
        return "Message sent to topic" + message;
    }
    @PostMapping("/sendSMS")
    public String sendSmSMessage(@RequestParam String message) {
        kafkaProducer.sendMessage("SmsTopic",message);
        return "Message sent to SmsTopic" + message;
    }
}
