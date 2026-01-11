package Logger.Kafka;

import Logger.DAO.Model.LogEntity;
import Logger.DAO.Model.LogMessage;
import Logger.DAO.Repository.LogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final LogRepository logRepository;

    public KafkaConsumer(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @KafkaListener(topics = "logs", groupId = "logger")
    public void consume(LogMessage message) {
        LogEntity entity = new LogEntity(
                message.type(),
                message.message()
        );
        logRepository.save(entity);
    }
}