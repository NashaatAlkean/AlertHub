package Processor.Service;

import Processor.DAO.Model.EmailTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
class EmailTopicService {

    @Bean
    public NewTopic EmailTopic() {
        return new NewTopic("EmailTopic", 1, (short) 1);
    }
    @Bean
    public NewTopic SmsTopic() {
        return new NewTopic("SmsTopic", 1, (short) 1);
    }
}
